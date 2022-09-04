import React, {Component} from 'react'

import {Modal, ModalBody, ModalFooter, ModalHeader} from 'reactstrap';
import { useState, useEffect } from "react";
import axios from "axios";


import '../components/movielist.css'

import { NavLink } from 'react-router-dom'

import {useTable, useGlobalFilter} from 'react-table'

export default function AboutPage(){


    const [movies, setMovies]= useState([]);
    const [tablaMovies, setTablaMovies]= useState([]);
    const [busqueda, setBusqueda]= useState("");
    
  
  const peticionGet=async()=>{
    await axios.get("http://localhost:3001/movies")
    .then(response=>{
      setMovies(response.data);
      setTablaMovies(response.data);
    }).catch(error=>{
      console.log(error);
    })
  }
  
  const handleChange=e=>{
    setBusqueda(e.target.value);
    filtrar(e.target.value);
  }
  
  //Filtrar por nombre, año, director o genero
  const filtrar=(terminoBusqueda)=>{
    var resultadosBusqueda=tablaMovies.filter((elemento)=>{
      if(elemento.name.toString().toLowerCase().includes(terminoBusqueda.toLowerCase())
      || elemento.director.toString().toLowerCase().includes(terminoBusqueda.toLowerCase())
      || elemento.genero.toString().toLowerCase().includes(terminoBusqueda.toLowerCase())
      ){
        return elemento;
      }
    });
    setMovies(resultadosBusqueda);
  }
  
  useEffect(()=>{
  peticionGet();
  },[])

    return (
        <body class="main-container">

            <div class="container-fluid text-center" >
                    <br></br>
                    <div class="th">
                        <NavLink to="/addmovie" class="thblanco">
                            ¿No encontró la pelicula? Añadala aquí
                        </NavLink>
                    </div>
                    <form>
                        <div class="input-group">
                            <input onChange={handleChange} value={busqueda} type="search" class="form-control rounded" placeholder="Buscar película por nombre, año, director o género" aria-label="Search" aria-describedby="search-addon"  name="Search"/>
                                <button class="btn btn-danger text-center mr-1" >
                                    Buscar
                                </button>
                        </div>
                    </form>
                <table class="table table-bordered table-striped table-hover">
                    <div class="container-fluid text-center row justify-content-start ">
                    </div>
                    <thead class="dark-thead">
                        <tr>
                            <th>Nombre</th>
                            <th>Fecha</th>
                            <th>Info</th>
                            <th>Calificar</th>
                        </tr>
                    </thead>
                    
                    <tbody>
                        {movies &&  movies.map(movies => (
                                <tr key={movies.name}>
                                <td >{movies.year}</td>
                                <td>{movies.name}</td>
                                <td>
                                    <NavLink to={"/moviedetailed/" + movies.id}>
                                        <button class="btn btn-info espacioizq">
                                            Información detallada
                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye" viewBox="0 0 16 16">
                                                <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                                <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                            </svg>
                                        </button>
                                    </NavLink>
                                </td>
                                <td>
                                    <NavLink to={"/MovieInfo/" + movies.id}>
                                        <button class="btn btn-info espacioizq">
                                            Calificar
                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star" viewBox="0 0 16 16">
                                                <path d="M2.866 14.85c-.078.444.36.791.746.593l4.39-2.256 4.389 2.256c.386.198.824-.149.746-.592l-.83-4.73 3.522-3.356c.33-.314.16-.888-.282-.95l-4.898-.696L8.465.792a.513.513 0 0 0-.927 0L5.354 5.12l-4.898.696c-.441.062-.612.636-.283.95l3.523 3.356-.83 4.73zm4.905-2.767-3.686 1.894.694-3.957a.565.565 0 0 0-.163-.505L1.71 6.745l4.052-.576a.525.525 0 0 0 .393-.288L8 2.223l1.847 3.658a.525.525 0 0 0 .393.288l4.052.575-2.906 2.77a.565.565 0 0 0-.163.506l.694 3.957-3.686-1.894a.503.503 0 0 0-.461 0z"/>
                                            </svg>
                                        </button>
                                    </NavLink>

                                </td>
                                </tr>

                            ))
                        }
                        <tr></tr>
                        <tr>
  

                        </tr>
                    </tbody>
                </table>

                
            </div>
        </body>

    )
}
