package com.github.paicoding.forum.web.controller.rank;

import com.github.paicoding.forum.api.model.enums.rank.ActivityRankTimeEnum;
import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.api.model.vo.rank.dto.RankInfoDTO;
import com.github.paicoding.forum.api.model.vo.rank.dto.RankItemDTO;
import com.github.paicoding.forum.service.rank.service.UserActivityRankService;
import com.github.paicoding.forum.web.global.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排行榜
 */
@RestController
public class RankController {
    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * 活跃用户排行榜
     *
     * @param time
     *
     * @return
     */
    @RequestMapping(path = "/rank/{time}")
    public ResultVo<RankInfoDTO> rank(@PathVariable(value = "time")String time) {
        ActivityRankTimeEnum rankTime = ActivityRankTimeEnum.nameOf(time);
        if (rankTime == null) {
            rankTime = ActivityRankTimeEnum.MONTH;
        }
        List<RankItemDTO> list = userActivityRankService.queryRankList(rankTime, 30);
        RankInfoDTO info = new RankInfoDTO();
        info.setItems(list);
        info.setTime(rankTime);
        return ResultVo.ok(info);
    }
}
