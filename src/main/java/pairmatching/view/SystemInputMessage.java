package pairmatching.view;

public enum SystemInputMessage {
    SELECT_FUNCTION("기능을 선택하세요.\n" +
            "1. 페어 매칭\n" +
            "2. 페어 조회\n" +
            "3. 페어 초기화\n" +
            "Q. 종료");

    private final String message;

    SystemInputMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
