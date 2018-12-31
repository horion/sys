package com.lf.helpdesk.help.repository;

import com.lf.helpdesk.help.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositoryTicket extends MongoRepository<Ticket,String> {


    Page<Ticket> findByUserIdOrderByDateDesc(Pageable pages, String userId);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc
            (String title,String status,String priority,Pageable pages);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc
            (String title, String status, String priority,String userId, Pageable pages);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserAssignedOrderByDateDesc
            (String title, String status, String priority, String userId, Pageable pages);

    Page<Ticket> findByNumber(Integer number,Pageable pages);


}
