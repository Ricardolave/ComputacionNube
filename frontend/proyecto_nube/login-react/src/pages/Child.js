import React from 'react'


import {BrowserRouter, Route, Routes, Link, Switch} from 'react-router-dom';


import HomePage from '../pages/HomePage'
import AboutPage from '../pages/AboutPage'
import LoginPage from '../pages/HomePage'
import SignUpPage from '../pages/HomePage'
import NotFoundPage from '../pages/HomePage'
import AddMovie from '../pages/HomePage'
import MovieInfo from '../pages/HomePage'
import MovieDetailedInfo from '../pages/HomePage'
import TestGetPage from '../pages/HomePage'
import PrivateRoute from '../pages/HomePage'
import global from '../pages/HomePage'


class Child extends React.Component{
    constructor(props){
        super(props);
    }
    
    render(){

        return(
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
        )
    }
}

export default Child;