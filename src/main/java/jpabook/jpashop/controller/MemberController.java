package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());     // memberForm에 MemberForm() 빈 껍데기를 담아서
        return "members/createMemberForm";                                     // members 밑의 createMemberForm.html로 전달하면 이제 화면에서 MemberForm 객체에 접근 가능해짐
    }

    /* 파라미터로 Member 엔티티를 사용하지 않고 MemberForm을 사용하는 이유는?
    * ---> Member 엔티티에는 당장 화면에서 필요한 데이터 말고 다른 데이터들이 더 들어있음.
    *       ex)  Member 엔티티에는 orders는 있고, @NotEmpty는 없음. 엔티티에 @NotEmpty를 추가하면 코드가 지저분해짐
    *           엄청 엄청 간단한 예제면 그냥 member 엔티티를 갖다 써도 되겠지만, 실무에서 쓰는 건 훨씬 더 복잡함.
    *           이런 이유로 그냥 차라리 화면에서 필요한 데이터만 있는 form을 새로 만드는 게 좋음.
    * */

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {    // @Valid: MemberForm @NotEmpty 같은 validation을 쓰고 싶을 때.
                                                                            // BindingResult: 검증 오류가 발생할 경우 오류 내용을 보관하는 스프링 프레임워크에서 제공하는 객체.
                                                                            // 검증 오류가 났을 때 "이름 빼먹었어요"하고 전 화면으로 돌아가게 하고 싶어서 사용.
                                                                            // @Valid 뒤에 써야 함.
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }
}
