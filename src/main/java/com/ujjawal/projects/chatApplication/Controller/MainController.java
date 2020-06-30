package com.ujjawal.projects.chatApplication.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.ujjawal.projects.chatApplication.Dto.LoginUserObject;
import com.ujjawal.projects.chatApplication.Dto.UserAddObject;
import com.ujjawal.projects.chatApplication.Entity.UserInfo;
import com.ujjawal.projects.chatApplication.Repo.UserRepository;

@Controller
public class MainController {

	Map<String, String> map = new HashMap<>();
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepo;
	@Autowired
	AuthenticationManager authenticationManager;

	@RequestMapping("/")
	public String index(HttpServletRequest request, Model model) {
		/*
		 * String username=(String) request.getSession().getAttribute("username");
		 * if(username==null || username.isEmpty()) { return "redirect:/login"; }
		 */
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userRepo.findByEmail(username);
		String userName = user.getFirstName() + " " + user.getLastName();

		model.addAttribute("username", userName);
		return "chat";
	}

	/*
	 * @RequestMapping(path = "/login", method = RequestMethod.GET) public String
	 * showLoginPage() { return "login"; }
	 */

	/*
	 * @RequestMapping(path = "/login", method = RequestMethod.POST) public String
	 * doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String
	 * username) { username = username.trim();
	 * 
	 * if (username.isEmpty()) { return "login"; }
	 * request.getSession().setAttribute("username", username);
	 * 
	 * return "redirect:/"; }
	 */

	/*
	 * @RequestMapping(path = "/logout") public String logout(HttpServletRequest
	 * request) { request.getSession(true).invalidate();
	 * 
	 * return "redirect:/login"; }
	 */

	@RequestMapping("/loginPage")
	public String showLogin(ModelMap modelMap) {
		if (map.size() > 0) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				modelMap.addAttribute(entry.getKey(), entry.getValue());
			}
			map.clear();
		}
		return "loginPage";

	}

	@PostMapping("/login")
	public String loginUser(LoginUserObject user) {

		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getEmail(),
					user.getPassword());
			org.springframework.security.core.Authentication auth = authenticationManager.authenticate(token);
			SecurityContext sc = SecurityContextHolder.getContext();

			sc.setAuthentication(auth);
		} catch (Exception e) {
			map.clear();
			map.put("emailError", "Invalid email");
			map.put("passwordError", "Invalid password");

			System.out.println("Login Failed");

			return "redirect:loginPage";
		}
		UserInfo savedUser = userRepo.findByEmail(user.getEmail());

		return "redirect:/";

	}

	@RequestMapping("/registerUser")
	public String showRegister(ModelMap modelMap) {
		if (map.size() > 0) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				modelMap.addAttribute(entry.getKey(), entry.getValue());
			}
			map.clear();
		}

		return "showAddUser";
	}

	@RequestMapping("/addUser")

	public String addUser(@Valid UserAddObject user, BindingResult bindingResult) throws IOException {
		
		UserInfo userExists = userRepo.findByEmail(user.getEmail()); 
		if(userExists!=null)
		{
			map.clear();
			map.put("email", "Email is already registered try to Login");
			return "redirect:registerUser";
			
		}
		if (bindingResult.hasErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			map.clear();

			for (FieldError e : errors) {

				map.put(e.getField(), e.getDefaultMessage());
			}

			return "redirect:registerUser";
		} else if (!user.getPassword().equals(user.getConfirmPassword())) {
			map.clear();

			map.put("confirmPassword", "Password doesn't match");
			return "redirect:registerUser";
		}

		else {
			UserInfo saveUser = new UserInfo();
			saveUser.setFirstName(user.getFirstName());
			saveUser.setLastName(user.getLastName());
			saveUser.setEmail(user.getEmail());
			saveUser.setPhoneNumber(user.getPhoneNumber());
			saveUser.setPassword(passwordEncoder.encode(user.getPassword()));

			userRepo.save(saveUser);

			return "redirect:loginPage";
		}

	}

}
