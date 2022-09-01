import React from 'react'
import { useState } from "react";
import axios from "axios";

import '../components/signup.css'

//Se importa NavLink para cambiar de ruta y no hacer un "refresh"
import { NavLink } from 'react-router-dom'

export default function SignUpPage(){

    return (
        <body class="main-container">
        <div class="container p-4">
            <div class="row">
                <div class="col-md-4 mx-auto">
                    <div class="card text-center">

                            <div class="card-body">
                                <h1 className="fs-4 card-title fw-bold mb-4" align="left">Registro</h1>
                                    <form class="espaciosup">
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Correo electrónico</label>
                                            <input id="email" type="text" className="form-control" name="usuario" required autoFocus />
                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Usuario</label>                                
                                            <input type="text" name="username"  class="form-control"/>
                                        </div>
                                        <div align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Contraseña</label>                                
                                            <input type="password" name="password" class="form-control" id="passwordInput"/>
                                        </div>
                                        <div class="mb-3" align="left">
                                            <label className="mb-2 text-muted" htmlFor="email" align="left">Repetir contraseña</label>                                
                                            <input type="password" name="password2" class="form-control" id="passwordInput"/>
                                        </div>

                                        <div class="espaciosup">
                                                <NavLink to="/login" className="float-end">
                                                    ¿Ya tiene una cuenta? Inicie sesión aquí
                                                </NavLink>
                                        </div><br></br>

                                        <div class="espaciosup" className="d-flex align-items-center">
                                            <button type="submit" className="btn btn-primary ms-auto">
                                                <i className="bi bi-box-arrow-in-right"></i> Registrarse
                                            </button>
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
