import { Link } from 'react-router-dom';
import React, { useState } from 'react'
import ProgressBar from './ProgressBar';

interface CardProps {
    courseId?: number;
    title: string;
    image?: string;
    description: string;
    language?: string[];
    level: string;
    techFocus?: string[];
    progressPercent?: number;
}

const MAX_TAGS = 3;

const CourseCard = ( { 
    title, 
    image, 
    description,
    language = [],
    level,
    techFocus = [],
    courseId,
    progressPercent
} : CardProps) => {

  const [showAll, setShowAll] = useState(false);

  const renderTags = (
    label: string,
    tags: string[],
    color: string,
    keyPrefix: string
  ) => {
    const displayTags = showAll ? tags : tags.slice(0, MAX_TAGS);
    const hiddenCount = tags.length - displayTags.length;

    return (
      <div className="mb-1">
        <div className="text-xs font-medium text-gray-500 mb-1">{label}</div>
        <div className="flex flex-wrap gap-1">
          {displayTags.map((tag, index) => (
            <span
              key={`${keyPrefix}-${index}`}
              className={`${color} text-xs px-2 py-0.5 rounded-full font-medium`}
            >
              {tag}
            </span>
          ))}
          {!showAll && hiddenCount > 0 && (
            <span
              className={`${color} text-xs px-2 py-0.5 rounded-full font-medium hover:bg-gray-300`}
            >
              +{hiddenCount} more
            </span>
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="rounded-2xl shadow-md hover:shadow-xl overflow-hidden bg-white transition-all duration-300">
      <Link to={`/course/overview/${courseId}`} className="block">
        <div className="p-4 space-y-2">
          {/* <img src={image} alt={title} className="w-full h-48 object-cover" /> */}
          <h3 className="text-lg font-semibold text-gray-800">{title}</h3>

          { renderTags("Level", [level.charAt(0).toUpperCase() + level.slice(1).toLowerCase()], "bg-yellow-100 text-yellow-700", "level")}
          { language && language.length > 0 && renderTags("Languages", language, "bg-green-100 text-green-700", "lang")}
          { techFocus && techFocus.length > 0 && renderTags("Technical Scope", techFocus, "bg-purple-100 text-purple-700 mb-2", "tech")}
          { progressPercent != null && <ProgressBar progressPercent={parseFloat(progressPercent.toFixed(2))} /> }
        </div>
      </Link>
    </div>
  )
}

export default CourseCard
