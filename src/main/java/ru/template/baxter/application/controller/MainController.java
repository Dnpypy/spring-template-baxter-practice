package ru.template.baxter.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.template.baxter.application.dao.AppUserDAO;
import ru.template.baxter.application.dao.CountryDAO;
import ru.template.baxter.application.formbean.AppUserForm;
import ru.template.baxter.application.model.AppUser;
import ru.template.baxter.application.model.Country;
import ru.template.baxter.application.validator.AppUserValidator;

import java.util.List;

@Controller

public class MainController {


    @Autowired
    private AppUserDAO appUserDAO;

    @Autowired
    private CountryDAO countryDAO;

    @Autowired
    private AppUserValidator appUserValidator;


    // Set a form validator
    @InitBinder
    protected void initBinder(WebDataBinder dataBinder) {
        // Form target
        Object target = dataBinder.getTarget();
        if (target == null) {
            return;
        }
        System.out.println("Target=" + target);

        if (target.getClass() == AppUserForm.class) {
            dataBinder.setValidator(appUserValidator);
        }
        // ...
    }

    @RequestMapping("/")
    public String viewHome(Model model) {

        return "welcomePage";
    }

    @GetMapping("/members")
    public String viewMembers(Model model) {

        List<AppUser> list = appUserDAO.getAppUsers();

        model.addAttribute("members", list);

        return "membersPage";
    }

    @RequestMapping("/registerSuccessful")
    public String viewRegisterSuccessful(Model model) {

        return "registerSuccessfulPage";
    }

    // Show Register page.
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String viewRegister(Model model) {

        AppUserForm form = new AppUserForm();
        List<Country> countries = countryDAO.getCountries();

        model.addAttribute("appUserForm", form);
        model.addAttribute("countries", countries);

        return "registerPage";
    }

    // This method is called to save the registration information.
    // @Validated: To ensure that this Form
    // has been Validated before this method is invoked.
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveRegister(Model model, //
                               @ModelAttribute("appUserForm") @Validated AppUserForm appUserForm, //
                               BindingResult result, //
                               final RedirectAttributes redirectAttributes) {

        // Validate result
        if (result.hasErrors()) {
            List<Country> countries = countryDAO.getCountries();
            model.addAttribute("countries", countries);
            return "registerPage";
        }
        AppUser newUser= null;
        try {
            newUser = appUserDAO.createAppUser(appUserForm);
        }
        // Other error!!
        catch (Exception e) {
            List<Country> countries = countryDAO.getCountries();
            model.addAttribute("countries", countries);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "registerPage";
        }

        redirectAttributes.addFlashAttribute("flashUser", newUser);

        return "redirect:/registerSuccessful";
    }

}
