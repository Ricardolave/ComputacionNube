import React from 'react'
import { useState, useEffect } from "react";
import axios from "axios";

import { Navigate, NavLink, useNavigate } from 'react-router-dom';

import '../components/movielist.css'

export default function AddMovie(){

    const navigate = useNavigate();

    const [moviedata, setDatos] = useState({
        name: "",
        year: "",
        elenco: "",
        director: "",
        genero: "",
        sinopsis:"",
      });

    const handleInputChange = (e) =>{

        let { name, value } = e.target;
        console.log(e.target)
        let newDatos = {...moviedata, [name]: value};
        setDatos(newDatos);
        console.log(newDatos)
    }
    
    const handleSubmit = async(e)=>{
        e.preventDefault();
        if(!e.target.checkValidity()){
          console.log("no enviar");
        }else{
          axios.post("http://localhost:3001/resenas/1",moviedata);
          navigate('/about')
        }
    };

    //Se toman los generos para ponerlos en una lista

    const [genero, setGenero]= useState([]);
    const [tablaGenero, setTablaGenero]= useState([]);
    
  
  const peticionGetGeneros=async()=>{
    await axios.get("http://localhost:3001/generos")
    .then(response=>{
      setGenero(response.data);
      setTablaGenero(response.data);
    }).catch(error=>{
      console.log(error);
    })
  }

  useEffect(()=>{
    peticionGetGeneros();
    },[])


    return (
        <body class="main-container">
        <div class="container p-4 col-auto ">
            <div class="row">
                <div class="col-md-4 mx-auto ">
                    <div class="card">
                        <div class="card-header bg-info text-center">
                            <h3 class="text-light">Nueva película</h3>
                        </div>
                        <div class="card-body">
                                <form onSubmit={handleSubmit} className="needs-validation" noValidate={true} autoComplete="off">
                                    <div class= "form-group">

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Título</label>
                                            <input type="text" class="form-control" name="name" id= "title" value = {moviedata.title} onChange={handleInputChange} autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Año de lanzamiento</label>                                
                                            <input type="text" class="form-control" name="year" id= "year" value = {moviedata.year} onChange={handleInputChange} autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Elenco</label>                                
                                            <input type="text" class="form-control" name="elenco" id= "elenco" value = {moviedata.elenco} onChange={handleInputChange} autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Director</label>                                
                                            <input type="text" class="form-control" name="director" id= "director" value = {moviedata.director} onChange={handleInputChange} autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Sinopsis</label>                                
                                            <input type="text" class="form-control" name="sinopsis" id= "sinopsis" value = {moviedata.sinopsis} onChange={handleInputChange} autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Género</label>                                
                                            <select onChange={handleInputChange} value={moviedata.genero} name="genero">
                                            {
                                                genero.map(genero => (
                                                    <option key={genero.id} value={genero.tipo}>{genero.tipo}</option>
                                                ))
                                            }
                                            </select>                                        
                                        </div>
                                    </div>

                                    <div class="form-group espaciosup2">
                                        <NavLink to="/about">
                                        <button class="btn btn-success btn-block bg-danger"> Cancelar</button>   
                                        </NavLink>
                                        <button class="btn btn-success btn-block bg-info" type='submit'> Añadir película</button>
                                    </div>
                                    
                                </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </body>

    )
}