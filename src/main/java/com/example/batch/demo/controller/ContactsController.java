package com.example.batch.demo.controller;

import com.example.batch.demo.core.repo.ContactRepository;
import com.example.batch.demo.model.Contact;
import jdk.nashorn.internal.runtime.arrays.IteratorAction;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RestController
@RequestMapping("/rest/v2")
public class ContactsController {

  private ContactRepository contactRepository;

  @Autowired
  public void setContactRepository(ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  @GetMapping("/contacts")
  public Iterable<Contact> findAll() {
    return contactRepository.findAll();
  }

  @DeleteMapping ("/contacts")
  public void delete()  {
    contactRepository.deleteAll();
  }

  @GetMapping("/contacts/{id}")
  public Contact find(@PathVariable Long id) {
    return contactRepository.findById(id).orElse(null);
  }

  @DeleteMapping ("/contacts/{id}")
  public void delete(@PathVariable Long id)  {
    contactRepository.deleteById(id);
  }
}
