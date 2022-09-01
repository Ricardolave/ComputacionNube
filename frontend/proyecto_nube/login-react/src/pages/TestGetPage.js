import axios from 'axios'
import React from 'react'

import { useState } from "react";

export default function TestGetPage(){

    axios.get('https://jsonplaceholder.typicode.com/users').then(resp => {
        console.log(resp.data);
    });

    return(
        <div>get</div>
    )
}
