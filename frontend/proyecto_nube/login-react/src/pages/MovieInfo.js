import React from 'react'
import {useParams} from 'react-router-dom'
import StarRating from "../elementos/StarRating";

export default function MovieInfo(){
    const {movieID} = useParams()

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
                                <form>
                                    <div class= "form-group">

                                        <div>
                                        <label className="mb-2 text-muted" htmlFor="email" align="left">Calificación</label>                                

                                        <StarRating />
                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Reseña</label>                                
                                            <textarea name="description" rows="2" class="form-control"></textarea>
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