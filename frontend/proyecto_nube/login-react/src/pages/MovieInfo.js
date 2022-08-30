import React from 'react'
import {useParams} from 'react-router-dom'

export default function MovieInfo(){
    const {movieID} = useParams()

    return (
        <div><h1>id: {movieID}</h1></div>
    )
}
