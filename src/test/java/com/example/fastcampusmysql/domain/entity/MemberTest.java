package com.example.fastcampusmysql.domain.entity;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.util.MemberFixtureFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class MemberTest {


    @DisplayName("회원 이름을 변경할 수 있습니다.")
    @Test
    public void changeNickNameTest() {
        // given
        Member member = MemberFixtureFactory.create();
        String expected = "hihi";

        // when
        member.changeNickname(expected);

        // then
        Assertions.assertEquals(expected, member.getNickname());
    }

    @DisplayName("회원의 이름은 10자 이내입니다.")
    @Test
    public void nickNameMaxLengthTest() {
        // given
        Member member = MemberFixtureFactory.create();
        String overMaxLengthName = "1234567890--";

        // when
        // then
        Assertions.assertThrows(IllegalArgumentException.class, () -> member.changeNickname(overMaxLengthName));
    }
}
