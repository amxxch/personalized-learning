import React, { ReactEventHandler } from 'react'

interface InputFieldProps {
    label: string;
    type?: string;
    Icon: React.ComponentType<React.SVGProps<SVGSVGElement>>;
    onChange: (value: string) => void;
}

const InputField = ({ label, type="text", Icon, onChange} : InputFieldProps) => {
    const handleInfoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        onChange(e.target.value);
    }

  return (
    <div className="relative mt-5 mb-5 max-w-[550px] border-b-2 border-orange-300">
        <Icon className='absolute right-2 top-3 text-xl' />
        <input
            type={type}
            className="peer w-full h-10 bg-transparent border-none outline-none px-6 pl-1"
            onChange={e => handleInfoChange(e)}
            required
            />
        <label className='absolute top-1/2 left-[5px] -translate-y-1/2 pointer-events-none transition-all duration-500 ease-in-out
                        peer-focus:top-[-5px] peer-focus:text-sm peer-valid:top-[-5px] peer-valid:text-sm'
        >
            {label}
        </label>
    </div>
  )
}

export default InputField
