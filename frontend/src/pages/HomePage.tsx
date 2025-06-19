import { useEffect, useState } from "react";
import CourseCard from "../components/CourseCard";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import { MdClear } from "react-icons/md";
import CourseCatalog from "../components/CourseCatalog";


export default function HomePage() {

  return (
    <div className="">
      <CourseCatalog />
    </div>
  );
}
