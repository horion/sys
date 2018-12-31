package com.lf.helpdesk.help.controller;

import com.lf.helpdesk.help.dto.Summary;
import com.lf.helpdesk.help.entity.ChangeStatus;
import com.lf.helpdesk.help.entity.Ticket;
import com.lf.helpdesk.help.entity.User;
import com.lf.helpdesk.help.enums.EnumProfile;
import com.lf.helpdesk.help.enums.EnumStatus;
import com.lf.helpdesk.help.response.Response;
import com.lf.helpdesk.help.security.jwt.JwtTokenUtil;
import com.lf.helpdesk.help.service.TicketService;
import com.lf.helpdesk.help.service.UserService;
import com.lf.helpdesk.help.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;


    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result){
        Response<Ticket> response = new Response<>();
        try{
            validateCreateTicket(ticket,result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            ticket.setStatus(EnumStatus.NEW);
            ticket.setUser(userFromRequest(request));
            ticket.setDate(new Date());
            ticket.setNumber(generateNumber());
            Ticket ticketPersisted =  this.ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
        Response<Ticket> response = new Response<>();
        try{
            validateUpdateTicket(ticket,result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Ticket ticketCurrent = ticketService.findById(ticket.getId());
            ticket.setStatus(ticketCurrent.getStatus());
            ticket.setUser(ticketCurrent.getUser());
            ticket.setDate(ticketCurrent.getDate());
            ticket.setNumber(ticketCurrent.getNumber());
            if(ticketCurrent.getUserAssigned() != null){
                ticket.setUserAssigned(ticketCurrent.getUserAssigned());
            }
            Ticket ticketPersisted =  this.ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id) {
        Response<Ticket> response = new Response<>();
        Ticket ticket = ticketService.findById(id);
        if(ticket == null){
            return ResponseEntity.noContent().build();
        }
        List<ChangeStatus> changeStatusList = new ArrayList<>();
        Iterable<ChangeStatus> changesCurrent = ticketService.listChangeStatus(id);
        for (Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext();) {
            ChangeStatus changeStatus = iterator.next();
            changeStatus.setTicket(null);
            changeStatusList.add(changeStatus);
        }
        ticket.setChanges(changeStatusList);
        response.setData(ticket);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<String>> deleteById(@PathVariable("id") String id) {
        Response<String> response = new Response<>();
        Ticket ticket = ticketService.findById(id);
        if(ticket == null){
            return ResponseEntity.noContent().build();
        }
        try {
            this.ticketService.delete(id);
            response.setData("Ticket deleted");
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("all/{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request,@PathVariable("page") int page,@PathVariable("count") int count){
        Response<Page<Ticket>> response = new Response<>();
        Page<Ticket> tickets;
        User userRequest = userFromRequest(request);
        if(userRequest.getProfile().equals(EnumProfile.ROLE_TECHNICIAN)){
            tickets = this.ticketService.listTicket(page,count);
            response.setData(tickets);
        }else if(userRequest.getProfile().equals(EnumProfile.ROLE_CUSTOMER)){
            tickets = this.ticketService.findByCurrentUser(page,count,userRequest.getId());
            response.setData(tickets);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/params/{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,@PathVariable("page") int page,@PathVariable("count") int count){
        Response<Page<Ticket>> response = new Response<>();
        String numberRequest = request.getHeader("number");
        String title = request.getHeader("title");
        String status = request.getHeader("status");
        String priority = request.getHeader("priority");
        String assignedRequest = request.getHeader("assigned");

        title = Util.isEmpty(title) ? "" : title;
        status = Util.isEmpty(status) ? "" : status;
        priority = Util.isEmpty(priority) ? "" : priority;
        boolean assigned = !Util.isEmpty(assignedRequest) && Boolean.getBoolean(assignedRequest);
        Integer number = Util.isEmpty(numberRequest) ? 0 : Integer.parseInt(numberRequest);

        Page<Ticket> tickets = null;
        if(number > 0){
            tickets = ticketService.findByNumber(page,count,number);
        }else{
            User user = userFromRequest(request);
            if(user.getProfile().equals(EnumProfile.ROLE_TECHNICIAN)){
                if(assigned){
                    tickets = ticketService.findByParameterAndAssignedUser(page,count,title,status,priority,user.getId());
                }else{
                    tickets = ticketService.findByParameters(page,count,title,status,priority);
                }
            }else if(user.getProfile().equals(EnumProfile.ROLE_CUSTOMER)){
                tickets = ticketService.findByParametersAndCurrentUser(page,count,user.getId(),title,status,priority);
            }
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }


    @PutMapping("status/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> updateStatus(HttpServletRequest request,@PathVariable("id") String id
            ,@RequestBody Ticket ticket,BindingResult result){

        String status = request.getHeader("status");
        status = Util.isEmpty(status) ? "" : status;
        Response<Ticket> response = new Response<>();
        try {
            validateChangeStatus(id,status,result);
            if(result.hasErrors()){
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            Ticket ticketCurrent = ticketService.findById(id);
            if(ticketCurrent == null){
                response.getErrors().add("Register not found id: "+id);
                return ResponseEntity.badRequest().body(response);
            }
            ticketCurrent.setStatus(EnumStatus.getStatus(status));
            if(EnumStatus.ASSIGNED.equals(EnumStatus.getStatus(status))){
                ticketCurrent.setUserAssigned(userFromRequest(request));
            }
            Ticket ticketPersisted = this.ticketService.createOrUpdate(ticket);
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUserChange(userFromRequest(request));
            changeStatus.setDate(new Date());
            changeStatus.setStatus(EnumStatus.getStatus(status));
            changeStatus.setTicket(ticketPersisted);
            ticketService.createChangeStatus(changeStatus);
            response.setData(ticketPersisted);
        }catch (Exception e){
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/summary")
    public ResponseEntity<Response<Summary>> findSummary(){
        Response<Summary> response = new Response<>();
        Summary summary;
        int amountNew=0;
        int amountResolved=0;
        int amountApproved=0;
        int amountDisapproved=0;
        int amountAssigned=0;
        int amountClosed=0;

        Iterable<Ticket> tickets = ticketService.findAll();
        if(tickets != null){
            for (Ticket ticket: tickets) {
                if(ticket.getStatus().equals(EnumStatus.NEW))
                    amountNew++;
                if(ticket.getStatus().equals(EnumStatus.RESOLVED))
                    amountResolved++;
                if(ticket.getStatus().equals(EnumStatus.APPROVED))
                    amountApproved++;
                if(ticket.getStatus().equals(EnumStatus.ASSIGNED))
                    amountAssigned++;
                if(ticket.getStatus().equals(EnumStatus.CLOSED))
                    amountClosed++;
                if(ticket.getStatus().equals(EnumStatus.DISAPPROVED))
                    amountDisapproved++;
            }
        }
        summary = new Summary(amountNew,amountResolved,amountApproved,amountDisapproved,amountAssigned,amountClosed);
        response.setData(summary);
        return ResponseEntity.ok(response);
    }



    private Integer generateNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    private User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUserNameFromToken(token);
        return userService.findByEmail(email);
    }

    private void validateCreateTicket(Ticket ticket, BindingResult result) {
        if(ticket.getTitle() == null){
            result.addError(new ObjectError("Ticket","Title no information"));
        }

    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result) {
        if(ticket.getId() == null){
            result.addError(new ObjectError("Ticket","Id no information"));
        }

        if(ticket.getTitle() == null){
            result.addError(new ObjectError("Ticket","Title no information"));
        }

    }

    private void validateChangeStatus(String id, String status, BindingResult result) {
        if(Util.isEmpty(id)){
            result.addError(new ObjectError("Ticket","Id no information"));
        }

        if(Util.isEmpty(status)){
            result.addError(new ObjectError("Ticket","Status no information"));
        }

    }
}
