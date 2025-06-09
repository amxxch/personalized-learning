// src/components/Layout.tsx
import React from 'react';
import NavBar from './components/Navbar';
import { useLocation } from 'react-router-dom';

export default function Layout({ children }: { children: React.ReactNode }) {
  const { pathname } = useLocation();
    const hasNavBar = !(pathname.includes('/login') || pathname.includes('/signup'));

  return (
    <div data-theme="cupcake">
      {hasNavBar && <NavBar />}
      <main className='h-screen'>{children}</main>
    </div>
  );
}
