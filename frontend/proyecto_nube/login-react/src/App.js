import { useState } from "react";
import axios from "axios";

//Se importa react-router-dom para las rutas
import {BrowserRouter, Route, Routes, Link, Switch} from 'react-router-dom';


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
import global from '../../login-react/src/pages/global'

//Se importan los componentes
import NavBar from '../../login-react/src/components/Navbar'

function App() {
  const [isAuth, setIsAuth] = useState(false);

  const [isLogged,setIsLogged] = useState(false);

  return (
    
    <div>
    <BrowserRouter>
    <Routes>
        <Route path="/" element={<LoginPage/>}/>

        <Route element={<PrivateRoute isLogged={true}/>}>
          <Route path="/about" element={<AboutPage/>}/>
          <Route path="/login" element={<LoginPage/>}></Route>
          <Route path="/signup" element={<SignUpPage/>}></Route>
          <Route path="/addmovie" element={<AddMovie/>}></Route>
          <Route path="/moviedetailed/:movieID" element={<MovieDetailedInfo/>}></Route>
          <Route path="/movieinfo/:movieID" element={<MovieInfo/>}></Route>
          <Route path="/test" element={<TestGetPage/>}></Route>
        </Route>

        <Route path="*" element={<NotFoundPage/>}></Route>
    </Routes>
    
    </BrowserRouter>

</div>
  );
}

export default App;
