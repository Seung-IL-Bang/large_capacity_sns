package com.example.fastcampusmysql.controller;

import com.example.fastcampusmysql.domain.dto.MemberDto;
import com.example.fastcampusmysql.domain.dto.MemberNicknameHistoryDto;
import com.example.fastcampusmysql.domain.dto.MemberRegisterCommand;
import com.example.fastcampusmysql.domain.entity.Member;
import com.example.fastcampusmysql.domain.service.MemberReadService;
import com.example.fastcampusmysql.domain.service.MemberWriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MemberController {
    final private MemberWriteService memberWriteService;
    final private MemberReadService memberReadService;

    @PostMapping("/members")
    public MemberDto register(@RequestBody MemberRegisterCommand command) {
        Member member = memberWriteService.register(command);
        return memberReadService.toDto(member);
    }


    @GetMapping("/members/{id}")
    public MemberDto getMember(@PathVariable Long id) {
        return memberReadService.getMember(id);
    }

    @PostMapping("{id}/name")
    public MemberDto changeNickName(@PathVariable Long id, @RequestBody String nickname) {
        memberWriteService.changeNickname(nickname, id);
        return memberReadService.getMember(id);
    }

    @GetMapping("/{memberId}/nickname-histories")
    public List<MemberNicknameHistoryDto> getNicknameHistories(@PathVariable Long memberId) {
        return memberReadService.getNicknameHistories(memberId);
    }
}
