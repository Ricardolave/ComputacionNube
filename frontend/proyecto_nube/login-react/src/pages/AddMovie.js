import React from 'react'

import '../components/movielist.css'

export default function AddMovie(){
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
                                <form>
                                    <div class= "form-group">

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Título</label>
                                            <input type="text" class="form-control" name="title" autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Año de lanzamiento</label>                                
                                            <input type="text" class="form-control" name="year" autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Elenco</label>                                
                                            <input type="text" class="form-control" name="elenco" autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Director</label>                                
                                            <input type="text" class="form-control" name="director" autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Género</label>                                
                                            <input type="text" class="form-control" name="genero" autofocus/>
                                        </div>

                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Reseña</label>                                
                                            <textarea name="description" rows="2" class="form-control"></textarea>
                                        </div>

                                    </div>

                                    <div class="form-group espaciosup2">
                                        <button class="btn btn-success btn-block bg-info"> Añadir película</button>
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