package sesac.server.user.dto.request;

public record SearchStudentRequest(
        String name,
        Long courseId,
        Integer status
) {

}
