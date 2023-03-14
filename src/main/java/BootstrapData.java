import com.project.anyahajo.model.*;
import com.project.anyahajo.repository.AppUserRepository;
import com.project.anyahajo.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
    public abstract class BootstrapData implements ItemRepository,CommandLineRunner{

//        private final AppUserRepository appUserRepository;            ez se kell mivel targyakkal foglalkozunk, nem userekkel
        private final ItemRepository itemRepository;
//    private Availability availability;
//    private Babycare babycare;
//    private Book book;
//    private Carrier carrier;
//    private CarrierType carrierType;
//    private Item item;
//    private Name name;
//    private Rent rent;
//    private User user;

//                       ezek nem kellenek

    public BootstrapData(AppUserRepository appUserRepository, ItemRepository itemRepository, Availability availability, Babycare babycare,
         Book book, Carrier carrier, CarrierType carrierType, Item item, Name name, Rent rent,  User user) {
        this.appUserRepository = appUserRepository;
        this.itemRepository = itemRepository;
        this.availability = availability;
        this.babycare = babycare;
        this.book = book;
        this.carrier = carrier;
        this.carrierType = carrierType;
        this.item = item;
        this.name = name;
        this.rent = rent;
        this.user = user;
    }

    @Override
        public void run(String... args) throws Exception {
//            Name eric = new Name();                 atneztem, ez a Userhez van, szoval itt nem kell
//            eric.setFirstName("Eric");
//            eric.setLastName("Evans");

        Book harryPotter = new Book();
//        bookname.setItem_id(1L);                   Ez automatikusan general az adatbazis
        harryPotter.setAuthor("J.K. Rowling");

        harryPotter.setName("Harry Potter and his magic rod");
        harryPotter.setAvailability(Availability.Available);
        harryPotter.setActive(true);
//        harryPotter.setPicture();                 eggyelore hadjuk ki
        harryPotter.setDescription("Harry Potter finds porn in the daily prophet");

//                                                  49 - 53 sorig mindegyik targynal ezek lesznek, csak az author valtozik
//                                          Carrier: setCarrierBrand(), setType(CarrierType. ), setSize()
//                                          Babycare: setBabycareBrand()

        itemRepository.save(harryPotter);

//        nem kell olyat beallitani ami nincs,pl publisher, bassza ra csak mukodjon, authornak is eleg nev

            Name rod = new Name();
            rod.setFirstName("Rod");
            rod.setLastName("Johnson");
/*
            Book noEJB = new Book();
            noEJB.setTitle("J2EE Development without EJB");
            noEJB.setIsbn("54757585");

            Author rodSaved = authorRepository.save(rod);
            Book noEJBSaved = bookRepository.save(noEJB);

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
