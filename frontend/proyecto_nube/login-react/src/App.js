import { useState } from "react";
import axios from "axios";
import React from 'react';

//Se importa react-router-dom para las rutas
import {BrowserRouter, Route, Routes, Link, Switch, NavLink, useNavigate} from 'react-router-dom';


//Se importan las rutas
import HomePage from '../../login-react/src/pages/HomePage'
import AboutPage from '../../login-react/src/pages/AboutPage'
import LoginPage from '../../login-react/src/pages/LoginPage'
import SignUpPage from '../../login-react/src/pages/SignUpPage'
import NotFoundPage from '../../login-react/src/pages/NotFoundPage'
import AddMovie from '../../login-react/src/pages/AddMovie'
import MovieInfo from '../../login-react/src/pages/MovieInfo'
import MovieDetailedInfo from '../../login-react/src/pages/MovieDetailedInfo'
import TestGetPage from '../../login-react/src/pages/TestGetPage'
import PrivateRoute from '../../login-react/src/pages/PrivateRoute'

//Se importan los componentes

function App() {


  const [isAuth, setIsAuth] = useState(true);

  const navigate = useNavigate();

  const [datos, setDatos] = useState({
    usuario: "",
    clave: ""
  });

  const handleInputChange = (e) =>{
    let { name, value } = e.target;
    let newDatos = {...datos, [name]: value};
    setDatos(newDatos);
  }

  const handleSubmit = async(e)=>{
    e.preventDefault();
    if(!e.target.checkValidity()){
      console.log("no enviar");
    }else{
      let res = await axios.post("http://localhost:8080/api/authenticate",datos)
      .then(response => {  
        setIsAuth(true)
        navigate('/about')
          const token = response.data.jwtToken
          localStorage.setItem("token", token);
          console.log(token);
      })

      .catch(response => {
    console.log(response)
    console.log(response.response.data.detail)
          alert(response.response.data.detail)})
    }
  };
  

  return (
    
    <div>

    <Routes>
        <Route element={<PrivateRoute isLogged={isAuth}/>}>
          <Route path="/" element={<Login/>}/>
          <Route path="/about" element={<AboutPage/>}/>
          <Route path="/login" element={<Login/>}></Route>
          <Route path="/signup" element={<SignUpPage/>}></Route>
          <Route path="/addmovie" element={<AddMovie/>}></Route>
          <Route path="/moviedetailed/:movieID" element={<MovieDetailedInfo/>}></Route>
          <Route path="/movieinfo/:movieID" element={<MovieInfo/>}></Route>
          <Route path="/test" element={<TestGetPage/>}></Route>
        </Route>
    </Routes>
    

  </div>
  );

  function Login(){

    return(
      <div>
              <div className="container h-100" class="centrado">
            <div className="row justify-content-sm-center h-100">
                <div className="col-xxl-4 col-xl-5 col-lg-5 col-md-7 col-sm-9">
                    <div className="card shadow-lg">
                        <div className="card-body p-5">
                            <h1 className="fs-4 card-title fw-bold mb-4">Login</h1>
                            <form onSubmit={handleSubmit} className="needs-validation" noValidate={true} autoComplete="off">
                                <div className="mb-3">
                                    <label className="mb-2 text-muted" htmlFor="email">Usuario</label>
                                    <input id="email" type="text" onChange={handleInputChange} value={datos.usuario} className="form-control" name="usuario" required autoFocus />
                                    <div className="invalid-feedback">
                                        Usuario inválido
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <div className="mb-2 w-100">
                                        <label className="text-muted" htmlFor="password">Contraseña</label>
                                        <a href="/" className="float-end">
                                            ¿Olvidaste tu contraseña?
                                        </a>
                                    </div>
                                    <input id="password" type="password" onChange={handleInputChange} value={datos.clave} className="form-control" name="clave" required />
                                    <div className="invalid-feedback">
                                        Contraseña es requirida
                                    </div>
                                </div>
                                <div className="d-flex align-items-center">
                                    <div className="form-check">
                                        <input type="checkbox" name="remember" id="remember" className="form-check-input" />
                                        <label htmlFor="remember" className="form-check-label">Recordarme</label>
                                    </div>
                                    <button type="submit" className="btn btn-primary ms-auto">
                                        <i className="bi bi-box-arrow-in-right"></i> Ingresar
                                    </button>
                                </div>

                                <div>
                                    <NavLink to="/signup" className="float-end">
                                        ¿No tiene una cuenta? Registrese aquí
                                    </NavLink>
                                </div>
                            </form>
                        </div>
                        <div className="card-footer py-3 border-0">
                            <div className="text-center">
                                Todos los derechos reservados &copy; 2022
                            </div>

                        </div>
                    </div>
                </div>
            </div>

        </div>
      </div>
    )
  }
}

export default App;
