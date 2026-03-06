package com.example.user;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @GET
    public List<User> all() {
        return User.listAll();
    }

    @GET
    @Path("/{id}")
    public User byId(@PathParam("id") Long id) {
        User u = User.findById(id);
        if (u == null) throw new NotFoundException();
        return u;
    }

    @POST
    @Transactional
    public User create(User dto) {
        if (User.find("email", dto.email).firstResult() != null) {
            throw new BadRequestException("User with email already exists");
        }
        dto.persist();
        return dto;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public User update(@PathParam("id") Long id, User dto) {
        User u = User.findById(id);
        if (u == null) throw new NotFoundException();
        u.name = dto.name;
        u.email = dto.email;
        return u;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        if (!User.deleteById(id)) throw new NotFoundException();
    }

    @GET
    @Path("/count")
    public String count() {
        return "Total users: " + User.count();
    }
}
