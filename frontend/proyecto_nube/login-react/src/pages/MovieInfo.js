import React from 'react'
import {useParams} from 'react-router-dom'
import StarRating from "../elementos/StarRating";
import { useState } from "react";
import axios from "axios";

export default function MovieInfo(){
    const {movieID} = useParams()

    const [movierating, setDatos] = useState({
        calificacion: "",
        reseña: ""
      });

    const handleInputChange = (e) =>{
        let { name, value } = e.target;
        let newDatos = {...movierating, [name]: value};
        setDatos(newDatos);
        console.log(newDatos)
    }
    
    const handleSubmit = async(e)=>{
        e.preventDefault();
        if(!e.target.checkValidity()){
          console.log("no enviar");
        }else{
          let res = await axios.post("http://localhost:3001/movieinfo/:id",movierating);
          console.log(res.data);
        }
    };

    return (
        <body class="main-container">
        <div class="container p-4 col-auto ">
            <div class="row">
                <div class="col-md-4 mx-auto ">
                    <div class="card">
                        <div class="card-header bg-info text-center">
                            <h3 class="text-light">Reseña de película</h3>
                        </div>
                        <div class="card-body">
                                <form onSubmit={handleSubmit} className="needs-validation" noValidate={true} autoComplete="off">
                                    <div class= "form-group">

                                        <div>
                                        <label className="mb-2 text-muted" htmlFor="email" align="left">Calificación</label>                                

                                        <StarRating />

                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Reseña</label>                                
                                            <input name="reseña" rows="2" class="form-control"  id= "reseña"  onChange={handleInputChange} autofocus></input>
                                        </div>

                                    </div>

                                    <div class="form-group espaciosup2">
                                        <button class="btn btn-success btn-block bg-info"> Añadir</button>
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