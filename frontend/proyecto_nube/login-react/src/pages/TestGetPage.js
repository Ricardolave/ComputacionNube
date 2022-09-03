import axios from 'axios'
import React from 'react'

import { useState, useEffect} from "react";

export default function TestGetPage(){
    
    const [posts, setPosts] = useState([])

    useEffect(() =>{
        axios.get('https://jsonplaceholder.typicode.com/users')
            .then(res => {
                console.log(res)
                setPosts(res.data)
            }).catch(err => {
                console.log(err)
            })
    })

    return(

        <div>

<table class="table table-dark">
  <thead>
    <tr>
      <th scope="col">#</th>
      <th scope="col">First</th>
      <th scope="col">Last</th>
      <th scope="col">Handle</th>
    </tr>
  </thead>
  <tbody>
  {
                posts.map(post => (
                    <tr>
                    <td key={post.id}>{post.id}</td>
                    </tr>

                ))
            }
    <tr>
      <th scope="row">2</th>
      <td>Jacob</td>
      <td>Thornton</td>
      <td>@fat</td>
    </tr>
    <tr>
      <th scope="row">3</th>
      <td>Larry</td>
      <td>the Bird</td>
      <td>@twitter</td>
    </tr>
  </tbody>
</table>

    </div>
    )
}
