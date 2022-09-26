import React from 'react'
import {useParams, useNavigate} from 'react-router-dom'
import StarRating from "../elementos/StarRating";
import { useState, useEffect, } from "react";
import axios from "axios";

export default function MovieInfo(){
    const navigate = useNavigate();
    const {movieID} = useParams()

    //Se solicitan los datos de la pelicula
    const [movie, setMovie] = useState(1)
    
    //Variables para dar la calificación
    const [movierating, setDatos] = useState({
        calificacion: "",
        comentario: "",
        pelicula:""
      });


    const peticionGet=async()=>{
        await axios.get(`http://localhost:8080/api/reviews/${movieID}`)
        .then(response=>{
	 setDatos(response.data);
        }).catch(error=>{
          console.log(error);
        })
      }
      
    useEffect(()=>{
        movierating.pelicula=movieID;
		movierating.calificacion=1;
		
        },[])


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
			console.log(movierating);
          let res = await axios
		  .post(`http://localhost:8080/api/reviews/`, movierating)
		  .then(response=>{
				alert("Reseña creada")
			    console.log(response.data);
				navigate('/about');
		  })
		  .catch(error=>{
			console.log(error);
			alert(error);
			});
         
        }
    };


    return (
        <body class="main-container">

        <div class="container p-4 col-auto ">
            <div class="row">
                <div class="col-md-4 mx-auto ">
                    <div class="card">
                        <div class="card-header bg-info text-center">
                            <h3 class="text-light">Calificando la película: </h3>
                        </div>
                        <div class="card-body">
                                <form onSubmit={handleSubmit} className="needs-validation" noValidate={true} autoComplete="off">
                                    <div class= "form-group">
                                        <div>
                                        <label className="mb-2 text-muted" htmlFor="email" align="left">Calificación</label>                                

                                        <select onChange={handleInputChange}  value={movierating.calificacion} name="calificacion">
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                        </select>
                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Reseña</label>                                
                                            <input name="reseña" rows="2" class="form-control"  id= "reseña"  onChange={handleInputChange} autofocus value={movierating.reseña}></input>
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