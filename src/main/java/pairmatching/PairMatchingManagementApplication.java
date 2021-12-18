package pairmatching;

import java.util.*;

import pairmatching.Configuration.DependencyInjection;
import pairmatching.System.SystemErrorMessage;
import pairmatching.System.SystemInputMessage;
import pairmatching.domain.Crew;
import pairmatching.dto.PropertyDto;
import pairmatching.service.MatchingService;
import pairmatching.utils.ParsingUtility;
import pairmatching.view.InputSystem;
import pairmatching.view.OutputSystem;

public class PairMatchingManagementApplication {

    private final static String BACK_END = "백엔드";
    private final static String FRONT_END = "프론트엔드";
    private final static String YES = "네";
    private final static String NO = "아니오";
    private final static int NOT_EXIST_MATCHING_RECODE = 0;
    private final static char PAIR_SELECT = '2';
    private final static char PAIR_RESET = '3';
    private final static char EXIT = 'Q';

    private final InputSystem inputSystem;
    private final OutputSystem outputSystem;
    private final ConstantDataStore constantDataStore;
    private final MatchingService matchingService;
    private final ParsingUtility parsingUtility;

    private Map<PropertyDto, String> matchingHistories;

    public PairMatchingManagementApplication() {
        DependencyInjection dependencyInjection = new DependencyInjection();
        inputSystem = dependencyInjection.inputSystem();
        outputSystem = dependencyInjection.outputSystem();
        constantDataStore = dependencyInjection.constantDataStore();
        matchingService = dependencyInjection.matchingService();
        parsingUtility=dependencyInjection.parsingUtility();
        matchingHistories = new HashMap<>();
    }

    public void start() {
        char inputFunctionNumber;
        List<Crew> crews = new ArrayList<>();
        PropertyDto previousPropertyDto = null;
        do {
            inputFunctionNumber = inputSystem.inputFunctionList();
            if (inputFunctionNumber == PAIR_SELECT && previousPropertyDto == null) {
                outputSystem.printConsoleMessage(SystemErrorMessage.NOT_MATCHING_HISTORY.getMessage());
                continue;
            } else if (inputFunctionNumber == PAIR_SELECT) {
                outputSystem.printConsoleMessage(matchingHistories.get(previousPropertyDto));
                continue;
            }
            if (inputFunctionNumber == PAIR_RESET) {
                matchingHistories = new HashMap<>();
                outputSystem.printConsoleMessage(SystemInputMessage.RESET.getMessage());
                continue;
            }
            String courseAndLevelAndMission = inputSystem.inputPropertyInput();
            PropertyDto propertyDto = parsingUtility.extractedPropertyInformation(courseAndLevelAndMission);
            crews = pickCrews(propertyDto.getCourse());
            if (!matchingHistories.containsKey(propertyDto)) {
                String matchingResult = matchingService.matchTheCrews(crews, propertyDto).toString();
                if (matchingResult.toString().equals(SystemErrorMessage.NOT_MATCHING.getMessage())) {
                    outputSystem.printConsoleMessage(matchingResult.toString());
                    continue;
                }
                previousPropertyDto = propertyDto;
                matchingHistories.put(propertyDto, SystemInputMessage.RESULT_PAIR_MATCHING.getMessage() + "\n" + matchingResult);
            } else {
                String inputYesOrNo = inputSystem.inputYesOrNo();
                if (inputYesOrNo.equals(YES)) {
                    matchingHistories.put(propertyDto, SystemInputMessage.RESULT_PAIR_MATCHING.getMessage() + "\n" + matchingService.matchTheCrews(crews, propertyDto).toString());
                    previousPropertyDto = propertyDto;
                } else if (inputYesOrNo.equals(NO)) {
                    continue;
                }
            }
            outputSystem.printConsoleMessage(matchingHistories.get(propertyDto));
        } while (inputFunctionNumber != EXIT);
    }

    private List<Crew> pickCrews(String course) {
        if (course.equals(BACK_END)) {
            return constantDataStore.getBackendCrews();
        } else if (course.equals(FRONT_END)) {
            return constantDataStore.getFrontedCrews();
        }
        return null;
    }
}
