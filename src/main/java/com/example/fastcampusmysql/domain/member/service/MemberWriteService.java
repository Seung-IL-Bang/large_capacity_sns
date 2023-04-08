package com.example.fastcampusmysql.domain.member.service;

import com.example.fastcampusmysql.domain.member.dto.MemberRegisterCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import com.example.fastcampusmysql.domain.member.repository.MemberNicknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberWriteService {

    final private MemberRepository memberRepository;
    final private MemberNicknameHistoryRepository memberNicknameHistoryRepository;

    @Transactional
    public Member register(MemberRegisterCommand command) {
        /**
         *  목표
         *      - 회원정보(이메일, 닉네임, 생년월일)을 등록한다.
         *      - 닉네임은 10자를 넘길 수 없다.
         *  파라미터
         *      - memberRegisterCommand
         *
         *  val member = Member.of(memberRegisterCommand)
         *  memberRepository.save(member)
         */

        var member = Member.builder()
                .nickname(command.nickname())
                .email(command.email())
                .birthDay(command.birthDay())
                .build();

        Member savedMember = memberRepository.save(member);
        saveMemberNicknameHistory(savedMember);
        return savedMember;
    }

    public void changeNickname(String newName, Long memberId) {
        /**
         * 1. 회원의 이름을 변경한다.
         * 2. 변경한 내역을 저장한다.
         */
        Member member = memberRepository.findById(memberId).orElseThrow();
        member.changeNickname(newName);
        memberRepository.save(member);

        // TODO: 변경내역 히스토리를 저장한다.
        saveMemberNicknameHistory(member);

    }

    private void saveMemberNicknameHistory(Member member) {
        MemberNicknameHistory history = MemberNicknameHistory.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .build();

        memberNicknameHistoryRepository.save(history);
    }
}
