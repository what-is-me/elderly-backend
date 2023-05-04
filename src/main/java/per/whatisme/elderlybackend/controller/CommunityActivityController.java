package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.CommunityActivity;
import per.whatisme.elderlybackend.repository.CommunityActivityRepository;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@Api(tags = "活动相关")
@RequestMapping("/api")
public class CommunityActivityController {
    @Autowired
    CommunityActivityRepository communityActivityRepository;

    @GetMapping({"/elderly/activities", "/admin/activities/"})
    Flux<CommunityActivity> findAll() {
        return communityActivityRepository.findAll();
    }

    @PostMapping("/admin/activities/")
    @ApiOperation(value = "保存活动", notes = "新增和修改放一起了，<br>新增的时候不要加id")
    Mono<CommunityActivity> saveActivity(@RequestBody CommunityActivity activity) {
        log.info(activity.toString());
        if (activity.getId() == null || activity.getId() == 0) {
            activity.setId(UidGenerator.generate());
        }
        return communityActivityRepository.save(activity);
    }

    @DeleteMapping("/admin/activities/")
    Mono<Void> deleteActivity(@RequestParam Long id) {
        return communityActivityRepository.deleteById(id);
    }
}
