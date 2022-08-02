package com.example.batch.demo.core;

import com.example.batch.demo.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import java.util.function.Predicate;

public class ContactValidator implements ItemProcessor<Contact, Contact> {

  private static final Logger log = LoggerFactory.getLogger(ContactValidator.class);

  @Override
  public Contact process(final Contact contact) throws Exception {
    if(firstNameVerifier.test(contact))
      return contact;

    log.info("Skipping contact (" + contact + ")");

    return null;
  }

  private static final Predicate<Contact> firstNameVerifier = (c) -> c.getFirstName().length()>3;
}
