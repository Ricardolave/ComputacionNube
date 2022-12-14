import React, {Component} from 'react'

import { useState, useEffect } from "react";
import axios from "axios";

import '../components/movielist.css'

import { NavLink, useParams } from 'react-router-dom'

export default function MovieDetailedPage(){


    //Se solicita la id mediante los parámetros
    const {movieID} = useParams();
	const [reviews, setReviews]= useState([]);

    //Se solicitan los datos de la pelicula
    const [movie, setMovie] = useState(1)

    const peticionGet=async()=>{
        await axios.get(`http://10.1.0.4:8080/api/movies/${movieID}`)
        .then(response=>{
          setMovie(response.data);
		  console.log(response.data.comentario)
		  setReviews(response.data.comentario)
        }).catch(error=>{
          console.log(error);
        })
      }
    useEffect(()=>{
        peticionGet();
        },[])

    return (
        <div class="container p-4">
            <div class="row">
                <div class="col-md-4 mx-auto">
                    <div class="card text-center">

                            <div class="card-body">
                                <h1 className="fs-4 card-title fw-bold mb-4" align="left">Info de película: {movie.name}</h1>
                                    <form class="espaciosup">
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Tíulo: {movie.name}</label>
                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Año de lanzamiento: {movie.year}</label>                                
                                        </div>                                      
                                        <div class="mb-3" align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Director: {movie.director}</label>                                
                                        </div>
                                        <div class="mb-3" align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Género: {movie.genero}</label>                                
                                        </div>
										<div>
										<h2 className="fs-4 card-title fw-bold mb-4" align="left">Reseñas</h2>
										<table class="table table-bordered table-striped table-hover">
					
										{reviews &&  reviews.map(review => (
											<tr key={review.id}>
											<td>{review.calificacion}</td>
											<td >{review.comentario}</td>
       
											</tr>

										))
										}							
          
										</table>
										</div>
											

                                        <div class="espaciosup">
                                                <NavLink to="/about" className="float-end">
                                                <button type="submit" className="btn btn-primary ms-auto">
                                                <i className="bi bi-box-arrow-in-right"></i> OK
                                            </button>
                                                </NavLink>
                                        </div><br></br>
                                    </form>
									
									
                            </div>
							
					
                    </div>
                </div>
            </div>
        </div>

    )
}