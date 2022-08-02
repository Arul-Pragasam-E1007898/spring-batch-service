package com.example.batch.demo.core;

import com.example.batch.demo.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ContactEnricher implements ItemProcessor<Contact, Contact> {

  private static final Logger log = LoggerFactory.getLogger(ContactEnricher.class);
  private static Long counter = 0L;

  @Override
  public Contact process(final Contact contact) throws Exception {
    final String firstName = contact.getFirstName().toUpperCase();
    final String lastName = contact.getLastName().toUpperCase();

    final Contact transformedContact = new Contact(firstName, lastName);
    transformedContact.setId(counter);
    counter++;

    log.info("Converting (" + contact + ") into (" + transformedContact + ")");

    return transformedContact;
  }

}
