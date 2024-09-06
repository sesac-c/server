package sesac.server.common.util;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import sesac.server.feed.entity.FeedType;
import sesac.server.group.entity.GroupType;

@ControllerAdvice
public class EnumBindingInitializer {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(FeedType.class, new EnumConverter(FeedType.class));
        binder.registerCustomEditor(GroupType.class, new EnumConverter(GroupType.class));
        // 추가적으로 다른 Enum 변환기를 등록할 수 있음
        // binder.registerCustomEditor(AnotherEnum.class, new EnumConverter(AnotherEnum.class));
    }
}
