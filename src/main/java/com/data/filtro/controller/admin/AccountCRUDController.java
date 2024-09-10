package com.data.filtro.controller.admin;

//@Controller
//@RequestMapping("/admin/account")
public class AccountCRUDController {
//
//    @Autowired
//    AccountService accountService;
//
//    @Autowired
//    UserService userService;
//
//    public Pageable sortAccount(int currentPage, int pageSize, int sortType) {
//        Pageable pageable;
//        switch (sortType) {
//            case 5, 10, 25, 50 -> pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("accountName"));
//            default -> {
//                pageSize = 5;
//                pageable = PageRequest.of(currentPage - 1, pageSize);
//            }
//        }
//        return pageable;
//    }
//
//    @GetMapping
//    public String show(@RequestParam(defaultValue = "5") int sortType, @RequestParam("currentPage") Optional<Integer> page, Model model, HttpSession session) {
//        Account admin = (Account) session.getAttribute("admin");
//        if (admin == null) {
//            return "redirect:/admin/login";
//        }
//        List<User> activeAccounts = userService.getActiveUser(1);
//        List<User> userAccounts = userService.getUserAccount(3);
//        int numberActiveAccounts = activeAccounts.size();
//        int numberUserAccounts = userAccounts.size();
//        int currentPage = page.orElse(1);
//        int pageSize = sortType;
//        Page<Account> accountPage;
//        Pageable pageable;
//        pageable = sortAccount(currentPage, pageSize, sortType);
//        accountPage = accountService.getAllPaging(pageable);
//        model.addAttribute("accounts", accountPage.getContent());
//        model.addAttribute("totalPages", accountPage.getTotalPages());
//        model.addAttribute("currentPage", currentPage);
//        model.addAttribute("totalElements", accountPage.getTotalElements());
//        model.addAttribute("sortType", sortType);
//        model.addAttribute("currentPage", currentPage);
//        model.addAttribute("numberActiveAccounts", numberActiveAccounts);
//        model.addAttribute("numberUserAccounts", numberUserAccounts);
//        return "admin/boot1/account";
//    }
//
//
//    @PostMapping("/create")
//    public String create(@ModelAttribute Account account) {
//        accountService.create(account);
//        return "redirect:/admin/account";
//    }
//
//    @PostMapping("/update")
//    public String update(@ModelAttribute Account account) {
//        accountService.update(account);
//        return "redirect:/admin/account";
//    }
//
//    @PostMapping("/delete")
//    public String delete(@RequestParam int id) {
//        accountService.delete(id);
//        return "redirect:/admin/account";
//    }


}
