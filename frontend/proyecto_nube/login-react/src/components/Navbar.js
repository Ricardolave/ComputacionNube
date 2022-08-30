import React from 'react'
import { NavLink } from 'react-router-dom'

import '../components/navbar.css'

const fondo = new URL('../components/images/wp.jpg', import.meta.url)
export default function NavBar(){
    return (
    <body>
    <div>
        <ul>
            <li>
                <NavLink 
                className={ ({isActive}) => (isActive ? 'active': "") } //Sirve para pintar el LI del navbar
                to='/about'
                >about
                </NavLink>
            </li>
            <li>
                <NavLink to="/login">
                Login
                </NavLink>
            </li>
        </ul>
    </div>
    </body>

    )
}