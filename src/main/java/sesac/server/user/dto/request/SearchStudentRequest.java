package sesac.server.user.dto.request;

public record SearchStudentRequest(
        String course,
        String name,
        Integer status
) {

}
