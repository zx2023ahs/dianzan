package cn.rh.flash.api.controller.frontapi;


import cn.rh.flash.bean.dto.api.PageDto;
import cn.rh.flash.bean.dto.api.TeamDto;
import cn.rh.flash.bean.dto.api.TeamTwoDto;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.api.GetTeamOneVo;
import cn.rh.flash.bean.vo.api.GetTeamTwoVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/tm")
@Api(tags = "团队信息")
public class TeamApi extends ContentApi{

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ConfigCache configCache;

    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "获取一级团队列表信息" , notes = "v1 版本")
    @PostMapping("/getTeamOne")
    public Ret< List<GetTeamOneVo> > getTeamOne( @Valid @RequestBody PageDto pageObj ) {
        return Rets.success( userInfoService.getTeamOne( getInvitationCode(), getLevels() ,pageObj) );
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "获取二级团队列表信息" , notes = "v1 版本")
    @PostMapping("/getTeamNewTwo")
    public Ret< List<GetTeamOneVo> > getTeamNewTwo( @Valid @RequestBody PageDto pageObj ) {
        return Rets.success( userInfoService.getTeamOne( pageObj.getInvitationCode(), pageObj.getLevels() ,pageObj) );
    }



    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "获三级团队列表信息" , notes = "v1 版本 [  invitationCode： 邀请码 ]")
    @PostMapping("/getTeamTwo")
    public Ret< List<GetTeamTwoVo> > getTeamTwo( @Valid @RequestBody TeamTwoDto teamTwoDto ) {
        return Rets.success( userInfoService.getTeamTwo( teamTwoDto  ) );
    }



    @ApiOperationSupport(author = "zx")
    @ApiOperation( value = "获取一级团队列表信息" , notes = "v2 版本")
    @PostMapping("/newGetTeamOne")
    public Ret< List<GetTeamOneVo> > newGetTeamOne( @Valid @RequestBody PageDto pageObj ) {
        return Rets.success( userInfoService.newGetTeamOne( getInvitationCode(), getLevels() ,pageObj) );
    }

    @ApiOperationSupport(author = "zx")
    @ApiOperation( value = "获取二级团队列表信息" , notes = "v2 版本")
    @PostMapping("/newGetTeamNewTwo")
    public Ret< List<GetTeamOneVo> > newGetTeamNewTwo( @Valid @RequestBody PageDto pageObj ) {
        return Rets.success( userInfoService.newGetTeamOne( pageObj.getInvitationCode(), pageObj.getLevels() ,pageObj) );

    }


    @ApiOperationSupport(author = "zx")
    @ApiOperation( value = "获三级团队列表信息" , notes = "v2 版本 [  invitationCode： 邀请码 ]")
    @PostMapping("/newGetTeamTwo")
    public Ret< List<GetTeamTwoVo> > newGetTeamTwo( @Valid @RequestBody TeamTwoDto teamTwoDto ) {
        return Rets.success( userInfoService.getTeamThree( teamTwoDto  ) );
    }

    @ApiOperationSupport(author = "zx")
    @ApiOperation( value = "获得团队开放级别" , notes = "v2 版本 [  invitationCode： 邀请码 ]")
    @PostMapping("/getTeamOpenLevel")
    public Ret getTeamOpenLeve() {
        //查询团队层级
         String trim = configCache.get(ConfigKeyEnum.TEAM_SIZE_HIREARCHY).trim();
        return Rets.success(trim);
    }

    @ApiOperationSupport(author = "yangfy")
    @ApiOperation( value = "获得团队查询项")
    @PostMapping("/getDicLevel")
    public Ret getDicLevel() {
        return Rets.success(ConstantFactory.me().getDicts("层级查询"));
    }


    @ApiOperationSupport(author = "zx")
    @ApiOperation( value = "获取各等级团队列表信息" , notes = "v2 版本")
    @PostMapping("/newGetTeam")
    public Ret< List<GetTeamOneVo> > newGetTeam( @Valid @RequestBody TeamDto teamDto ) {
        return Rets.success( userInfoService.newGetTeam( getInvitationCode(), getLevels() ,teamDto) );
    }

}
