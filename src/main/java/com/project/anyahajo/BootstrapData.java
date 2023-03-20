package com.project.anyahajo;

import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public  class BootstrapData implements CommandLineRunner{
    private final ItemRepository itemRepository;
    public BootstrapData(UserRepository appUserRepository, ItemRepository itemRepository) {

        this.itemRepository = itemRepository;
    }
    @Override
    public void run(String... args) throws Exception {

        itemRepository.deleteAll();

        Book harryPotter = new Book();
        harryPotter.setAuthor("J.K. Rowling");
        harryPotter.setName("Harry Potter and his magic rod");
        harryPotter.setAvailability(Availability.Available);
        harryPotter.setActive(true);
        harryPotter.setDescription("DHarry Potter finds porn in the daily prophet");

        Book littleprince = new Book();
        littleprince.setAuthor("Antoine de");
        littleprince.setName("The Little Prince");
        littleprince.setAvailability(Availability.Reserved);
        littleprince.setActive(true);
        littleprince.setDescription("Book that great for adults and childrens too");
        Book winnie = new Book();
        winnie.setAuthor("Alan Alexander");
        winnie.setName("Winnie the pooh");
        winnie.setAvailability(Availability.NotAvailable);
        winnie.setActive(false);
        winnie.setDescription("Simple but lovelable story");

        itemRepository.save(harryPotter);
        itemRepository.save(littleprince);
        itemRepository.save(winnie);

        Babycare powder=new Babycare();
        powder.setBabycareBrand("Hellobello");
        powder.setName("Premium baby powder");
        powder.setAvailability(Availability.Available);
        powder.setActive(true);
        powder.setDescription("Prémium baba por");

        Babycare oil=new Babycare();
        oil.setBabycareBrand("Rituals");
        oil.setName("Simple Baby oil");
        oil.setAvailability(Availability.Reserved);
        oil.setActive(true);
        oil.setDescription("Szimpla baba olaj");

        Babycare wipes=new Babycare();
        wipes.setBabycareBrand("Chicco");
        wipes.setName("Baby Wipes");
        wipes.setAvailability(Availability.NotAvailable);
        wipes.setActive(false);
        wipes.setDescription("Baba törlő kendők ");
        itemRepository.save(powder);
        itemRepository.save(oil);
        itemRepository.save(wipes);

        Carrier wraps=new Carrier();
        wraps.setCarrierBrand("Meitais");
        wraps.setType(CarrierType.Wrap);
        wraps.setSize("small");
        wraps.setName("Small wrap");
        wraps.setAvailability(Availability.Available);
        wraps.setActive(true);
        wraps.setDescription("Kis baba kendő");

        Carrier buckle=new Carrier();
        buckle.setCarrierBrand("Hellobello");
        buckle.setType(CarrierType.Buckle);
        buckle.setSize("medium");
        buckle.setName("Medium buckle");
        buckle.setAvailability(Availability.Reserved);
        buckle.setActive(true);
        buckle.setDescription("Közép méretű csatos hordozó");

        Carrier others=new Carrier();
        others.setCarrierBrand("Rituals");
        others.setType(CarrierType.OtherShaped);
        others.setSize("large");
        others.setName("Large type of carrier");
        others.setAvailability(Availability.NotAvailable);
        others.setActive(false);
        others.setDescription("Egyedi formájú nagy méretű hordozó");
        itemRepository.save(wraps);
        itemRepository.save(buckle);
        itemRepository.save(others);

    }
}
