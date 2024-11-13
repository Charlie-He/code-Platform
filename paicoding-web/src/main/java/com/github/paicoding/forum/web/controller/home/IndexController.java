package com.github.paicoding.forum.web.controller.home;

import com.github.paicoding.forum.api.model.vo.ResVo;
import com.github.paicoding.forum.web.controller.home.helper.IndexRecommendHelper;
import com.github.paicoding.forum.web.controller.home.vo.IndexVo;
import com.github.paicoding.forum.web.global.vo.ResultVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Resource
    private IndexRecommendHelper indexRecommendHelper;
    @GetMapping("/index")
    public ResultVo<IndexVo> index(HttpServletRequest request){
        String category=request.getParameter("category");
        IndexVo vo=indexRecommendHelper.buildIndexVo(category);
        return ResultVo.ok(vo);
    }
}
