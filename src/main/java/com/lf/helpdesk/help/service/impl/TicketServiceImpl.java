package com.lf.helpdesk.help.service.impl;

import com.lf.helpdesk.help.entity.ChangeStatus;
import com.lf.helpdesk.help.entity.Ticket;
import com.lf.helpdesk.help.repository.RepositoryChangeStatus;
import com.lf.helpdesk.help.repository.RepositoryTicket;
import com.lf.helpdesk.help.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    private final RepositoryTicket repositoryTicket;
    private final RepositoryChangeStatus repositoryChangeStatus;

    @Autowired
    public TicketServiceImpl(RepositoryTicket repositoryTicket, RepositoryChangeStatus repositoryChangeStatus) {
        this.repositoryTicket = repositoryTicket;
        this.repositoryChangeStatus = repositoryChangeStatus;
    }


    @Override
    public Ticket createOrUpdate(Ticket ticket) {
        return this.repositoryTicket.save(ticket);
    }

    @Override
    public Ticket findById(String id) {
        return this.repositoryTicket.findById(id).orElse(null);
    }

    @Override
    public void delete(String id) {
        this.repositoryTicket.deleteById(id);
    }

    @Override
    public Page<Ticket> listTicket(int page, int count) {
        Pageable pageable = PageRequest.of(page,count);
        return this.repositoryTicket.findAll(pageable);
    }

    @Override
    public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
        return this.repositoryChangeStatus.save(changeStatus);
    }

    @Override
    public Iterable<ChangeStatus> listChangeStatus(String ticketId) {
        return this.repositoryChangeStatus.findByTicketIdOrderByDateDesc(ticketId);
    }

    @Override
    public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
        Pageable pageable = PageRequest.of(page,count);
        return this.repositoryTicket.findByUserIdOrderByDateDesc(pageable,userId);
    }

    @Override
    public Page<Ticket> findByParameters(int page, int count, String title,String status,String priority) {
        Pageable pageable = PageRequest.of(page,count);
        return this.repositoryTicket.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title,status,priority,pageable);
    }

    @Override
    public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String userId, String title,String status,String priority) {
        Pageable pageable = PageRequest.of(page,count);

        return this.repositoryTicket.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title,status,priority,userId,pageable);
    }

    @Override
    public Page<Ticket> findByNumber(int page, int count, Integer number) {
        Pageable pageable = PageRequest.of(page,count);
        return this.repositoryTicket.findByNumber(number,pageable);
    }

    @Override
    public Iterable<Ticket> findAll() {
        return this.repositoryTicket.findAll();
    }

    @Override
    public Page<Ticket> findByParameterAndAssignedUser(int page, int count, String userId, String title,String status,String priority) {
        Pageable pageable = PageRequest.of(page,count);
        return this.repositoryTicket.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserAssignedOrderByDateDesc(title,status,priority,userId,pageable);
    }
}
