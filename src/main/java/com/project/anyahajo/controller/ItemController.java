package com.project.anyahajo.controller;

import com.project.anyahajo.form.ItemForm;
import com.project.anyahajo.model.Item;
import com.project.anyahajo.repository.ItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    @NonNull
    private ItemRepository itemRepository;

    @GetMapping(path = {"/kolcsonzes"})
    public String listItems(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "all-items";
    }

    @GetMapping(path = {"/admin/ujTargyFelvetel"})
    public String newItem(Model model){
        model.addAttribute("newItem", new ItemForm());
        return "admin-add-item";
    }

    @PostMapping(path = {"/admin/ujTargyFelvetel"})
    public String saveItem(
            @ModelAttribute("newItem")
            @Validated
            ItemForm itemForm,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()){
            return "admin-add-item";
        }

        Item entity = new Item();
        entity.setName(itemForm.getName());
        entity.setDateOfRent(itemForm.getDateOfRent());

        return "redirect:/kolcsonzes";
    }

}
