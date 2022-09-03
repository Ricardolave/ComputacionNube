import { useState } from "react";
import axios from "axios";

//Se importa react-router-dom para las rutas
import {BrowserRouter, Route, Routes} from 'react-router-dom';


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

//Se importan los componentes
import NavBar from '../../login-react/src/components/Navbar'

function App() {

  return (
    
    <div>
    <BrowserRouter>
    <Routes>
        <Route path="/" element={<LoginPage/>}></Route>
        <Route path="/login" element={<LoginPage/>}></Route>
        <Route path="/signup" element={<SignUpPage/>}></Route>
        <Route path="/about" element={<AboutPage/>}></Route>
        <Route path="/addmovie" element={<AddMovie/>}></Route>
        <Route path="/moviedetailed/:movieID" element={<MovieDetailedInfo/>}></Route>
        <Route path="/movieinfo/:movieID" element={<MovieInfo/>}></Route>
        <Route path="/test" element={<TestGetPage/>}></Route>
        <Route path="*" element={<NotFoundPage/>}></Route>
        
    </Routes>
    
    </BrowserRouter>

</div>
  );
}

export default App;
