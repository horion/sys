package com.lf.helpdesk.help.service;


import com.lf.helpdesk.help.entity.ChangeStatus;
import com.lf.helpdesk.help.entity.Ticket;
import org.springframework.data.domain.Page;

public interface TicketService {


    Ticket createOrUpdate(Ticket ticket);

    Ticket findById(String id);

    void delete(String id);

    Page<Ticket> listTicket(int page,int count);

    ChangeStatus createChangeStatus(ChangeStatus changeStatus);

    Iterable<ChangeStatus> listChangeStatus(String ticketId);

    Page<Ticket> findByCurrentUser(int page,int count,String userId);

    Page<Ticket> findByParameters(int page,int count,String title,String status,String priority);

    Page<Ticket> findByParametersAndCurrentUser(int page,int count,String userId,String title,String status,String priority);

    Page<Ticket> findByNumber(int page,int count,Integer number);

    Iterable<Ticket> findAll();

    Page<Ticket> findByParameterAndAssignedUser(int page,int count,String userId,String title,String status,String priority);


}
