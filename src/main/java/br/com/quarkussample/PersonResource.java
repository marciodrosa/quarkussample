package br.com.quarkussample;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {

    @GET
    public List<Person> list() {
        return Person.listAll();
    }

    @GET
    @Path("/{id}")
    public Person get(Long id) {
        return Person.findById(id);
    }

    @POST
    @Transactional
    public Response create(Person person) {
        person.persist();
        return Response.created(URI.create("/people/" + person.id)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Person update(Long id, Person person) {
        Person entity = Person.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.name = person.name;
        entity.birth = person.birth;
        entity.status = person.status;
        entity.persist();
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(Long id) {
        Person entity = Person.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.delete();
    }

    @GET
    @Path("/search/{name}")
    public Person search(String name) {
        return Person.findByName(name);
    }

    @GET
    @Path("/count")
    public Long count() {
        return Person.count();
    }
}
