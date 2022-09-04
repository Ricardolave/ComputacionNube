import React from 'react'
import { useState } from "react";
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
        let newDatos = {...moviedata, [name]: value};
        setDatos(newDatos);
        console.log(newDatos)
    }
    
    const handleSubmit = async(e)=>{
        e.preventDefault();
        if(!e.target.checkValidity()){
          console.log("no enviar");
        }else{
          let res = await axios.post("http://localhost:3001/movies",moviedata);
          console.log(res.data);
          navigate('/about')
        }
    };
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
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Género</label>                                
                                            <input type="text" class="form-control" name="genero" id= "genero" value = {moviedata.genero} onChange={handleInputChange}autofocus/>
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