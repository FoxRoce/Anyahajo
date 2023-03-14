package com.project.anyahajo;

import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.AppUserRepository;
import com.project.anyahajo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public  class BootstrapData implements CommandLineRunner{

    //        private final AppUserRepository appUserRepository;            ez se kell mivel targyakkal foglalkozunk, nem userekkel
    private final ItemRepository itemRepository;
   // private final AppUserRepository appUserRepository;

    public BootstrapData(AppUserRepository appUserRepository, ItemRepository itemRepository) {
      //  this.appUserRepository = appUserRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//            Name eric = new Name();                 atneztem, ez a Userhez van, szoval itt nem kell
//            eric.setFirstName("Eric");
//            eric.setLastName("Evans");

//        bookname.setItem_id(1L);                   Ez automatikusan general az adatbazis
        Book harryPotter = new Book();
        harryPotter.setAuthor("J.K. Rowling");
        harryPotter.setName("Harry Potter and his magic rod");
        harryPotter.setAvailability(Availability.Available);
        harryPotter.setActive(true);
        harryPotter.setDescription("DHarry Potter finds porn in the daily prophet");
//        harryPotter.setPicture();                 eggyelore hadjuk ki
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

//                                                  49 - 53 sorig mindegyik targynal ezek lesznek, csak az author valtozik
//                                          Carrier: setCarrierBrand(), setType(CarrierType. ), setSize()
//                                          Babycare: setBabycareBrand()

        itemRepository.save(harryPotter);
        itemRepository.save(littleprince);
        itemRepository.save(winnie);
//        nem kell olyat beallitani ami nincs,pl publisher, bassza ra csak mukodjon, authornak is eleg nev
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
        itemRepository.save(wipes);
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

/*
            ericSaved.getBooks().add(dddSaved);
            rodSaved.getBooks().add(noEJBSaved);
            dddSaved.getAuthors().add(ericSaved);
            noEJBSaved.getAuthors().add(rodSaved);

            Publisher publisher = new Publisher();
            publisher.setPublisherName("My Publisher");
            publisher.setAddress("123 Main");
            Publisher savedPublisher = publisherRepository.save(publisher);

            dddSaved.setPublisher(savedPublisher);
            noEJBSaved.setPublisher(savedPublisher);

            authorRepository.save(ericSaved);
            authorRepository.save(rodSaved);
            bookRepository.save(dddSaved);
            bookRepository.save(noEJBSaved);

            System.out.println("In Bootstrap");
            System.out.println("Author Count: " + authorRepository.count());
            System.out.println("Book Count: " + bookRepository.count());

            System.out.println("Publisher Count: " + publisherRepository.count());*/
    }
}
