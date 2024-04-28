package com.example;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;



        @Path("/movies")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)

        public class MovieResource {

        @Inject MovieRepository movieRepository;

        @GET
        public Response getAll(){
            List<Movie> movies= movieRepository.listAll();
            return Response.ok(movies).build();
        }

        //method to fetch the movie by id
        @GET
        @Path("{id}")
        public Response getById(@PathParam("id") Long id){
            return movieRepository.findByIdOptional(id)
                    .map(movie -> Response.ok(movie).build())
              .orElse(Response.status(NOT_FOUND).build());
        }
            @PUT
            @Path("{id}")
            @Transactional

            public Response updateById(@PathParam("id") Long id, Movie movie) {
                return movieRepository
                        .find("id",id)
                        .singleResultOptional()
                        .map(
                                m -> {
                                    if (movie.getTitle() != null) {
                                        m.setTitle(movie.getTitle());
                                    }
                                    if (movie.getCategory() != null) {
                                        m.setCategory(movie.getCategory());
                                    }

                                    return Response.ok(m).build();
                                })
                        .orElse(Response.status(NOT_FOUND).build());
            }

    //method to fetch the movie by Title

            @GET
            @Path("title/{title}")
            public Response getByTitle(@PathParam("title") String title){
                return movieRepository.find("title",title)
                        .singleResultOptional()
                        .map(movie-> Response.ok(movie).build())
                        .orElse(Response.status(NOT_FOUND).build());
            }

        // fetch movies by category
        @GET
        @Path("category/{category}")
        public Response getByCategory(@PathParam("category") String category){
            List<Movie> movies= movieRepository.list("category",category);
            return Response.ok(movies).build();

        }

        // method to create new movie
        @POST
        @Transactional
        public Response create(Movie movie){
            movieRepository.persist(movie);
            if (movieRepository.isPersistent(movie)){
                return Response.created(URI.create("/movies/"+movie.getId())).build();
            }
            return  Response.status(BAD_REQUEST).build();
        }

        // method to delete a movie by id
        @DELETE
        @Path("{id}")
        @Transactional
        public Response deleteById(@PathParam("id") Long id){
            boolean deleted = movieRepository.deleteById(id);
            return deleted ? Response.noContent().build()
                    : Response.status(NOT_FOUND).build();
        }
    }



