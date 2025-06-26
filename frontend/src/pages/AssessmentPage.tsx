import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";

import { useAuth } from "../context/AuthContext";
import { QuizChoice, QuizQuestion, SelectedAnswer, QuizSolution } from "../dto/response";
import LoadingSpinner from "../components/LoadingSpinner";

const AssessmentPage = () => {

    const { userId } = useAuth();
    const { courseId } = useParams();
    const navigate = useNavigate();

    const paginationLength = 5;
    const [numOfQuestions, setNumOfQuestions] = useState(10);
    const [maxPageTab, setMaxPageTab] = useState(0);
    const [questions, setQuestions] = useState<QuizQuestion[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [currentQuestion, setCurrentQuestion] = useState(1);    // 1-indexed
    const [currentPageTab, setCurrentPageTab] = useState(0);
    const [selectedAnswers, setSelectedAnswers] = useState<SelectedAnswer[]>([]);

    const [submitted, setSubmitted] = useState(false);

    // Fetch initial assessment questions
    useEffect(() => {
        axios.get('http://localhost:8080/api/v1/initial-assessment', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            },
            params: {
                courseId: courseId,
                userId: userId
            }
        })
        .then(response => {
            const data = response.data;
            console.log(data);
            setNumOfQuestions(data.length);
            setMaxPageTab(Math.floor((data.length - 1) / paginationLength));

            const questions = data.map((q: QuizQuestion) => ({
                questionId: q.questionId,
                question: q.question,
                difficulty: q.difficulty,
                quizChoices: q.quizChoices.map((choice: QuizChoice) => ({
                    choiceLetter: choice.choiceLetter,
                    content: choice.content
                }))
            }));

            questions.map((q: QuizQuestion) => {
                q.quizChoices.push({
                    choiceLetter: 'E',
                    content: "I'm not sure"
                });
            })

            questions.sort((a: QuizQuestion, b: QuizQuestion) => {
                return a.questionId - b.questionId;
            });

            console.log("Fetched questions: ", questions);

            setQuestions(questions);
            setIsLoading(false);
        })
        .catch(error => {
            console.error('Error fetching profile setup data:', error);
        });
        }, [userId]);
    
    // Handle answer selection
    const handleSelect = (questionId: number, choiceIndex: number) => {
        setSelectedAnswers(prev => {
            const existingAnswer = prev.find(answer => answer.questionId === questionId);
            if (existingAnswer) {
                return prev.map(answer =>
                    answer.questionId === questionId
                    ? { ...answer, choiceIndex }
                    : answer
                );
            } else {
                return [...prev, { questionId, choiceIndex }];
            }
        }
        )
        };
        
    // Handle answer submission and render solutions
    const handleSubmit = () => {
        setIsLoading(true);
        const answers = selectedAnswers.map(answer => ({
            questionId: answer.questionId,
            choiceLetterStr: questions.find(q => q.questionId === answer.questionId)?.quizChoices[answer.choiceIndex].choiceLetter
        }));

        if (answers.length !== numOfQuestions) {
            alert('Please answer all questions!');
            setIsLoading(false);
            return;
        }

        console.log('Submitting answers:', answers);

        axios.post('http://localhost:8080/api/v1/initial-assessment/submit', {
            userId,
            courseId,
            qnaList: answers
        }, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        })
        .then(response => {
            const data = response.data;
            console.log('Assessment submitted successfully:', data);
            data.sort((a: QuizSolution, b: QuizSolution) => a.questionId - b.questionId);
            const addedSolution = selectedAnswers.map((answer: SelectedAnswer) => {
                const question = data.find((q: QuizSolution) => q.questionId === answer.questionId);
                if (question) {
                    answer.correctIndex = question.correctChoice.charCodeAt(0) - 'A'.charCodeAt(0);
                    console.log(`Question ID: ${question.questionId}, Correct Index: ${answer.correctIndex}, Selected Index: ${answer.choiceIndex}, isCorrect: ${question.correctChoice === question.selectedChoice}`);
                    answer.isCorrect = question.correctChoice === question.selectedChoice;
                }
                return answer;
            })
            setSelectedAnswers(addedSolution);
            setSubmitted(true);
            setIsLoading(false);
        })
        .catch(error => {
            console.error('Error submitting assessment:', error);
        });
    };

    const handlePagination = (direction: 'next' | 'prev') => {
        const newPageTab = Math.min(
        Math.max(currentPageTab + (direction === 'next' ? 1 : -1), 0),
        maxPageTab
        );
    
        setCurrentPageTab(newPageTab);
    
        const newQuestion = newPageTab * paginationLength + 1;
        setCurrentQuestion(newQuestion);
    };

    // Temporary
    useEffect(() => {
        console.log("SelectedAnswers:", selectedAnswers);
    }, [selectedAnswers]);

    const randomSubmit = () => {
        const randomAnswers = questions.map(q => {
            const randomIndex = Math.floor(Math.random() * q.quizChoices.length);
            return {
                questionId: q.questionId,
                choiceIndex: randomIndex
            };
        });

        setSelectedAnswers(randomAnswers);
        handleSubmit();
    }
        

    return (
        <div className="min-h-screen flex flex-col items-center px-4">

            { isLoading && <LoadingSpinner message="Loading questions..." /> }

            { !isLoading && questions.length > 0 && !submitted && (
            <div>
                <h1 className="text-4xl font-mono font-bold mt-12 text-center">Initial Assessment</h1>
                {/* Pagination Controls */}
                <div className="flex justify-center">

                    {/* Temporary */}
                    <button className="btn btn-outline btn-secondary mt-6 mb-4" onClick={() => randomSubmit()}>
                        Skip Assessment
                    </button>

                    <div className="join my-6">
                        <button
                        className="join-item btn bg-white hover:bg-gray-300"
                        onClick={() => handlePagination('prev')}
                        >
                        «
                        </button>

                        {Array.from({ length: paginationLength }, (_, index) => {
                        const questionNumber = currentPageTab * paginationLength + index + 1;
                        if (questionNumber > numOfQuestions) return null;

                        return (
                            <input
                            key={questionNumber}
                            className={`join-item btn btn-square ${
                                currentQuestion === questionNumber
                                ? 'btn-primary'
                                : 'bg-white hover:bg-blue-100'
                            }`}
                            type="radio"
                            name="options"
                            aria-label={`${questionNumber}`}
                            onClick={() => setCurrentQuestion(questionNumber)}
                            />
                        );
                        })}

                        <button
                        className="join-item btn bg-white hover:bg-gray-300"
                        onClick={() => handlePagination('next')}
                        >
                        »
                        </button>
                    </div>
                </div>

                {/* Quiz Box */}
                <div className="max-w-3xl w-[2500px] overflow-y-auto p-6 bg-white shadow-lg rounded-xl">
                    {questions.map((q, index) => (
                    <div
                        key={index}
                        className={`mb-6 ${currentQuestion === index + 1 ? 'block' : 'hidden'}`}
                    >
                        
                        <div className="text-xl font-semibold mb-4 break-words">
                            <ReactMarkdown>{`${index + 1} . ${q.question}`}</ReactMarkdown>
                        </div>
                        <div className="space-y-3">
                        {q.quizChoices.map((choice, index) => (
                            <div
                            key={index}
                            onClick={() => handleSelect(q.questionId, index)}
                            className={`cursor-pointer px-4 py-3 border rounded-xl transition ${
                                selectedAnswers.find(answer => answer.questionId === q.questionId && answer.choiceIndex === index)
                                ? 'bg-pink-100 border-pink-500'
                                : 'bg-gray-50 hover:bg-gray-100 border-gray-300'
                            }`}
                            >
                            <span className="font-bold mr-2">{choice.choiceLetter}:</span>{choice.content}
                            </div>
                        ))}
                        </div>
                    </div>
                    ))}

                    {/* Display submit button when reaching the last question */}
                    {currentQuestion === numOfQuestions && (
                    <button
                        onClick={handleSubmit}
                        disabled={selectedAnswers.length !== numOfQuestions}
                        className="mt-6 w-full bg-pink-700 text-white font-semibold py-2 px-4 rounded-xl disabled:opacity-50"
                    >
                        Submit
                    </button>
                    )}
                </div>
            </div>
            )}

            {/* Display results after submission */}
            { submitted && (
                <div>
                    <h1 className="text-4xl font-mono font-bold mt-12 text-center text-pink-700">
                        Your Results
                    </h1>

                    {/* Result Summary */}
                    <div className="max-w-3xl w-full p-8 bg-white shadow-2xl rounded-2xl mt-8">
                    <div className="text-2xl font-semibold mb-3 text-gray-800 text-center">
                        🎯 You answered {selectedAnswers.filter(ans => ans.isCorrect).length} out of {numOfQuestions} questions correctly.
                    </div>

                    <div className="text-gray-600 text-md mb-6 text-center">
                        Awesome effort! Based on your performance, we've tailored your personalized learning path.
                        Ready to continue your journey?
                    </div>

                    <button
                        onClick={() => navigate(`/course/overview/${courseId}`)}
                        className="w-full bg-pink-600 hover:bg-pink-700 transition text-white font-semibold py-3 px-4 rounded-xl mb-6 text-lg"
                    >
                        🚀 Start Learning
                    </button>

                    <div className="text-gray-600 text-md text-center mb-2 font-medium">
                        Want to see what you got right and wrong?
                    </div>

                    
                    <div className="text-center text-sm text-gray-500">
                    Take a moment to review your answers below. <br />
                    Don’t worry if you missed some — the upcoming lessons will guide you through everything you need to know!
                    </div>
                </div>


                {/* Solutions */}
                <div className="max-w-3xl w-[2500px] overflow-y-auto p-6 bg-white shadow-lg rounded-xl mt-6">
                    {questions.map((q, index) => {
                    const answer = selectedAnswers.find(ans => ans.questionId === q.questionId);

                    return (
                        <div key={index} className="mt-4 mb-12">
                        <div className="text-xl font-semibold mb-4 break-words">
                            <ReactMarkdown>{`${index + 1} . ${q.question}`}</ReactMarkdown>
                        </div>
                        <div className="space-y-3">
                            {q.quizChoices.map((choice, idx) => {
                            let bgClass = 'bg-gray-50 hover:bg-gray-100 border-gray-300';

                            if (answer) {
                                if (idx === answer.correctIndex) {
                                bgClass = 'bg-green-100 border-green-500'; // correct answer
                                } else if (idx === answer.choiceIndex && !answer.isCorrect) {
                                bgClass = 'bg-red-100 border-red-500'; // wrong answer selected
                                }
                            }

                            return (
                                <div
                                key={idx}
                                className={`cursor-pointer px-4 py-3 border rounded-xl transition ${bgClass}`}
                                >
                                <span className="font-bold mr-2">{choice.choiceLetter}:</span>
                                {choice.content}
                                </div>
                            );
                            })}
                        </div>
                        </div>
                    );
                    })}
                </div>
            </div>
            )}
        </div>

    );
};

export default AssessmentPage;
