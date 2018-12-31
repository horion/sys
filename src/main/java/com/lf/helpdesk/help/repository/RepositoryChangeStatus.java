package com.lf.helpdesk.help.repository;

import com.lf.helpdesk.help.entity.ChangeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryChangeStatus extends MongoRepository<ChangeStatus,String> {


    Iterable<ChangeStatus> findByTicketIdOrderByDateDesc(String ticketId);



}
