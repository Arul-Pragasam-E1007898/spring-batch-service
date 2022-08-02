package com.example.batch.demo.core;

import com.example.batch.demo.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.atomic.AtomicLong;

public class ContactEnricher implements ItemProcessor<Contact, Contact> {

  private static final Logger log = LoggerFactory.getLogger(ContactEnricher.class);
  private static final AtomicLong counter = new AtomicLong(0L);

  @Override
  public Contact process(final Contact contact) throws Exception {
    final String firstName = contact.getFirstName().toUpperCase();
    final String lastName = contact.getLastName().toUpperCase();

    final Contact transformedContact = new Contact(firstName, lastName);
    transformedContact.setId(counter.incrementAndGet());

    log.info("Converting (" + contact + ") into (" + transformedContact + ")");

    return transformedContact;
  }

}
