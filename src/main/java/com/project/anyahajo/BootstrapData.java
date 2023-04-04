package com.project.anyahajo;

import com.project.anyahajo.model.*;
import com.project.anyahajo.service.ItemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public  class BootstrapData implements CommandLineRunner{

    @NonNull
    private final ItemService itemService;

    @Override
    public void run(String... args) throws Exception {

//        itemRepository.deleteAll();

        Book harryPotter = new Book();
        harryPotter.setAuthor("J.K. Rowling");
        harryPotter.setName("Harry Potter and his magic rod");
        harryPotter.setAvailability(Availability.Available);
        harryPotter.setActive(true);
        harryPotter.setDescription("Harry Potter finds porn in the daily prophet");


        Book littleprince = new Book();
        littleprince.setAuthor("Antoine de");
        littleprince.setName("The Little Prince");
        littleprince.setAvailability(Availability.Available);
        littleprince.setActive(true);
        littleprince.setDescription("Book that great for adults and childrens too");

        Book winnie = new Book();
        winnie.setAuthor("Alan Alexander");
        winnie.setName("Winnie the pooh");
        winnie.setAvailability(Availability.Available);
        winnie.setActive(false);
        winnie.setDescription("Simple but lovelable story");

        itemService.save(harryPotter);
        itemService.save(littleprince);
        itemService.save(winnie);

        Babycare powder=new Babycare();
        powder.setBabycareBrand("Hellobello");
        powder.setName("Premium baby powder");
        powder.setAvailability(Availability.Available);
        powder.setActive(true);
        powder.setDescription("Prémium baba por");

        Babycare oil=new Babycare();
        oil.setBabycareBrand("Rituals");
        oil.setName("Simple Baby oil");
        oil.setAvailability(Availability.Available);
        oil.setActive(true);
        oil.setDescription("Szimpla baba olaj");

        Babycare wipes=new Babycare();
        wipes.setBabycareBrand("Chicco");
        wipes.setName("Baby Wipes");
        wipes.setAvailability(Availability.Available);
        wipes.setActive(false);
        wipes.setDescription("Baba törlő kendők ");
        itemService.save(powder);
        itemService.save(oil);
        itemService.save(wipes);

        Carrier wraps=new Carrier();
        wraps.setCarrierBrand("Meitais");
        wraps.setCarrierType(CarrierType.Wrap);
        wraps.setSize("small");
        wraps.setName("Small wrap");
        wraps.setAvailability(Availability.Available);
        wraps.setActive(true);
        wraps.setDescription("Kis baba kendő");

        Carrier buckle=new Carrier();
        buckle.setCarrierBrand("Hellobello");
        buckle.setCarrierType(CarrierType.Buckle);
        buckle.setSize("medium");
        buckle.setName("Medium buckle");
        buckle.setAvailability(Availability.Available);
        buckle.setActive(true);
        buckle.setDescription("Közép méretű csatos hordozó");

        Carrier others=new Carrier();
        others.setCarrierBrand("Rituals");
        others.setCarrierType(CarrierType.OtherShaped);
        others.setSize("large");
        others.setName("Large type of carrier");
        others.setAvailability(Availability.Available);
        others.setActive(false);
        others.setDescription("Egyedi formájú nagy méretű hordozó");
        itemService.save(wraps);
        itemService.save(buckle);
        itemService.save(others);

    }
}
