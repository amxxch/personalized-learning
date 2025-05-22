import { Link } from 'react-router-dom';
import React from 'react'

interface CardProps {
    title: string;
    image: string;
    description: string;
    tags?: string[];
}

const CourseCard = ( { 
    title, 
    image, 
    description,
    tags = []
} : CardProps) => {

  return (
    <div className="rounded-2xl shadow-lg overflow-hidden bg-white transition hover:shadow-xl">
      <Link to={`/courses/${title}/chat`} className="block">
        <img src={image} alt={title} className="w-full h-48 object-cover" />
        <div className="p-4">
          <h3 className="text-lg text-black font-semibold mb-1">{title}</h3>

          {tags.length > 0 && (
            <div className="flex mb-1">
              {tags.map((tag, index) => (
                <span key={index} className="border border-blue-500 bg-blue-50 text-blue-500 text-xs font-semibold px-2 rounded-3xl mr-1">
                  {tag}
                </span>
              ))}
            </div>
          )}
          <p className="text-sm text-gray-600">{description}</p>
        </div>
      </Link>
  </div>
  )
}

export default CourseCard
