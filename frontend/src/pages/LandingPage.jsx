import { useNavigate } from 'react-router-dom';
import { 
  Package, ShoppingCart, BarChart3, Users, Shield, Clock, 
  CheckCircle, ArrowRight, TrendingUp, Zap, Globe, Lock,
  Star, MessageSquare, ChevronDown
} from 'lucide-react';
import Button from '../components/common/Button';
import { useState } from 'react';

const LandingPage = () => {
  const navigate = useNavigate();
  const [openFaq, setOpenFaq] = useState(null);

  const features = [
    {
      icon: Package,
      title: 'Smart Inventory',
      description: 'AI-powered stock predictions, automated reordering, and real-time tracking across all locations.',
      color: 'from-blue-500 to-cyan-500',
    },
    {
      icon: ShoppingCart,
      title: 'Order Management',
      description: 'End-to-end order processing with automated workflows, status tracking, and customer notifications.',
      color: 'from-purple-500 to-pink-500',
    },
    {
      icon: BarChart3,
      title: 'Advanced Analytics',
      description: 'Powerful insights with customizable dashboards, trend analysis, and predictive forecasting.',
      color: 'from-orange-500 to-red-500',
    },
    {
      icon: Users,
      title: 'Multi-Organization',
      description: 'Manage multiple businesses with complete data isolation, custom branding, and separate billing.',
      color: 'from-green-500 to-emerald-500',
    },
    {
      icon: Shield,
      title: 'Security & Permissions',
      description: 'Enterprise-grade security with granular role-based access, audit logs, and compliance tools.',
      color: 'from-indigo-500 to-purple-500',
    },
    {
      icon: Zap,
      title: 'Real-Time Sync',
      description: 'Instant updates across all devices with automatic conflict resolution and offline support.',
      color: 'from-yellow-500 to-orange-500',
    },
  ];

  const benefits = [
    { icon: TrendingUp, text: 'Reduce inventory costs by up to 35%' },
    { icon: Zap, text: 'Save 15+ hours per week on manual tasks' },
    { icon: CheckCircle, text: 'Prevent stockouts with AI predictions' },
    { icon: Globe, text: 'Scale to unlimited products & locations' },
    { icon: Lock, text: 'Bank-level security & data encryption' },
    { icon: Users, text: '24/7 expert support & onboarding' },
  ];

  const testimonials = [
    {
      name: 'Sarah Johnson',
      role: 'Operations Manager',
      company: 'TechStore Inc.',
      content: 'IMS transformed our inventory management. We reduced costs by 40% and eliminated stockouts completely.',
      rating: 5,
      image: '👩‍💼',
    },
    {
      name: 'Michael Chen',
      role: 'CEO',
      company: 'Global Retail Co.',
      content: 'The best inventory system we\'ve used. Real-time insights help us make better decisions every day.',
      rating: 5,
      image: '👨‍💼',
    },
    {
      name: 'Emily Rodriguez',
      role: 'Warehouse Manager',
      company: 'FastShip Logistics',
      content: 'Order processing is now 3x faster. The automation features are a game-changer for our team.',
      rating: 5,
      image: '👩‍💻',
    },
  ];

  const faqs = [
    {
      question: 'How long does it take to set up?',
      answer: 'Most businesses are up and running within 24 hours. Our onboarding team will guide you through the entire process.',
    },
    {
      question: 'Can I import my existing inventory data?',
      answer: 'Yes! We support CSV imports and can integrate with most existing systems. Our team will help you migrate your data seamlessly.',
    },
    {
      question: 'Is my data secure?',
      answer: 'Absolutely. We use bank-level encryption, regular security audits, and comply with SOC 2, GDPR, and HIPAA standards.',
    },
    {
      question: 'What kind of support do you offer?',
      answer: '24/7 customer support via chat, email, and phone. Plus dedicated account managers for enterprise plans.',
    },
    {
      question: 'Can I customize the system for my business?',
      answer: 'Yes! The system is highly customizable with custom fields, workflows, reports, and integrations via our API.',
    },
  ];

  const pricingFeatures = [
    'Unlimited products & orders',
    'Real-time analytics dashboard',
    'Multi-location support',
    'Role-based access control',
    'Mobile apps (iOS & Android)',
    'API access & integrations',
  ];

  const scrollToSection = (id) => {
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  return (
    <div className="min-h-screen bg-white">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-md shadow-sm sticky top-0 z-50 border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center space-x-3">
              <div className="bg-gradient-to-br from-primary-500 to-secondary-500 p-2 rounded-xl shadow-lg">
                <Package className="h-6 w-6 text-white" />
              </div>
              <div>
                <span className="text-2xl font-bold bg-gradient-to-r from-primary-600 to-secondary-600 bg-clip-text text-transparent">
                  IMS
                </span>
                <p className="text-xs text-gray-500">Inventory Management</p>
              </div>
            </div>
            <nav className="hidden md:flex items-center space-x-8">
              <button onClick={() => scrollToSection('features')} className="text-gray-600 hover:text-primary-600 transition-colors">
                Features
              </button>
              <button onClick={() => scrollToSection('testimonials')} className="text-gray-600 hover:text-primary-600 transition-colors">
                Testimonials
              </button>
              <button onClick={() => scrollToSection('pricing')} className="text-gray-600 hover:text-primary-600 transition-colors">
                Pricing
              </button>
              <button onClick={() => scrollToSection('faq')} className="text-gray-600 hover:text-primary-600 transition-colors">
                FAQ
              </button>
            </nav>
            <Button onClick={() => navigate('/login')} variant="primary" className="shadow-lg">
              Get Started
            </Button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative py-24 px-4 sm:px-6 lg:px-8 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-primary-50 via-purple-50 to-secondary-50"></div>
        
        {/* Animated background elements */}
        <div className="absolute top-20 left-10 w-72 h-72 bg-primary-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
        <div className="absolute top-40 right-10 w-72 h-72 bg-secondary-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse" style={{ animationDelay: '1s' }}></div>
        <div className="absolute bottom-20 left-1/2 w-72 h-72 bg-purple-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse" style={{ animationDelay: '2s' }}></div>
        
        <div className="relative max-w-7xl mx-auto">
          <div className="text-center max-w-5xl mx-auto">
            <div className="inline-flex items-center space-x-2 bg-white/80 backdrop-blur-sm px-4 py-2 rounded-full shadow-lg mb-8 border border-primary-100">
              <Star className="h-4 w-4 text-yellow-500 fill-current" />
              <span className="text-sm font-medium text-gray-700">Trusted by 500+ businesses worldwide</span>
            </div>
            
            <h1 className="text-6xl md:text-7xl font-extrabold text-gray-900 mb-8 leading-tight">
              Inventory Management
              <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary-600 via-purple-600 to-secondary-600">
                Made Simple
              </span>
            </h1>
            
            <p className="text-2xl text-gray-600 mb-12 leading-relaxed max-w-3xl mx-auto">
              The all-in-one platform to track inventory, process orders, and grow your business with confidence.
              <span className="block mt-2 text-lg text-gray-500">No credit card required • Free 14-day trial</span>
            </p>
            
            <div className="flex flex-col sm:flex-row gap-6 justify-center mb-12">
              <Button
                onClick={() => navigate('/login')}
                variant="primary"
                className="text-lg px-10 py-5 shadow-2xl hover:shadow-primary-500/50 hover:scale-105 transition-all duration-300 group"
              >
                Start Free Trial
                <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
              </Button>
              <Button
                onClick={() => scrollToSection('features')}
                variant="outline"
                className="text-lg px-10 py-5 bg-white/80 backdrop-blur-sm hover:bg-white transition-all"
              >
                Explore Features
              </Button>
            </div>
            
            <div className="grid grid-cols-2 md:grid-cols-4 gap-8 max-w-3xl mx-auto pt-12 border-t border-gray-200">
              <div>
                <p className="text-3xl font-bold text-primary-600">10K+</p>
                <p className="text-sm text-gray-600 mt-1">Products Managed</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-primary-600">50K+</p>
                <p className="text-sm text-gray-600 mt-1">Orders Processed</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-primary-600">500+</p>
                <p className="text-sm text-gray-600 mt-1">Happy Clients</p>
              </div>
              <div>
                <p className="text-3xl font-bold text-primary-600">99.9%</p>
                <p className="text-sm text-gray-600 mt-1">Uptime SLA</p>
              </div>
            </div>
          </div>
        </div>
        
        <button 
          onClick={() => scrollToSection('features')}
          className="absolute bottom-10 left-1/2 transform -translate-x-1/2 animate-bounce"
        >
          <ChevronDown className="h-8 w-8 text-primary-600" />
        </button>
      </section>

      {/* Features Section */}
      <section id="features" className="py-24 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-white to-gray-50">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-20">
            <div className="inline-block mb-4">
              <span className="bg-primary-100 text-primary-700 text-sm font-semibold px-4 py-1.5 rounded-full">
                Features
              </span>
            </div>
            <h2 className="text-5xl font-bold text-gray-900 mb-6">
              Everything You Need to Succeed
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Powerful tools designed to streamline your operations and drive growth
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <div
                  key={index}
                  className="group relative p-8 bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-500 hover:-translate-y-2 border border-gray-100 overflow-hidden"
                >
                  <div className={`absolute inset-0 bg-gradient-to-br ${feature.color} opacity-0 group-hover:opacity-5 transition-opacity duration-500`}></div>
                  
                  <div className={`relative w-14 h-14 bg-gradient-to-br ${feature.color} rounded-xl flex items-center justify-center mb-5 group-hover:scale-110 group-hover:rotate-3 transition-transform duration-300 shadow-lg`}>
                    <Icon className="h-7 w-7 text-white" />
                  </div>
                  
                  <h3 className="text-xl font-bold text-gray-900 mb-3 group-hover:text-primary-600 transition-colors">
                    {feature.title}
                  </h3>
                  <p className="text-gray-600 leading-relaxed">
                    {feature.description}
                  </p>
                  
                  <div className="mt-4 opacity-0 group-hover:opacity-100 transition-opacity">
                    <span className="text-primary-600 text-sm font-semibold inline-flex items-center">
                      Learn more <ArrowRight className="ml-1 h-4 w-4" />
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="py-24 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
            <div>
              <div className="inline-block mb-4">
                <span className="bg-green-100 text-green-700 text-sm font-semibold px-4 py-1.5 rounded-full">
                  Benefits
                </span>
              </div>
              <h2 className="text-5xl font-bold text-gray-900 mb-6">
                Transform Your Business Operations
              </h2>
              <p className="text-xl text-gray-600 mb-10 leading-relaxed">
                Join industry leaders who have revolutionized their inventory management with our platform
              </p>
              <div className="space-y-5">
                {benefits.map((benefit, index) => {
                  const Icon = benefit.icon;
                  return (
                    <div
                      key={index}
                      className="flex items-start p-4 rounded-xl hover:bg-primary-50 transition-colors group"
                    >
                      <div className="w-12 h-12 bg-gradient-to-br from-green-400 to-emerald-500 rounded-xl flex items-center justify-center mr-4 flex-shrink-0 group-hover:scale-110 transition-transform shadow-lg">
                        <Icon className="h-6 w-6 text-white" />
                      </div>
                      <div>
                        <span className="text-lg font-semibold text-gray-900 group-hover:text-primary-600 transition-colors">
                          {benefit.text}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
              <div className="mt-10">
                <Button
                  onClick={() => navigate('/login')}
                  variant="primary"
                  className="text-lg px-8 py-4 shadow-xl hover:shadow-2xl transition-shadow"
                >
                  See It In Action
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Button>
              </div>
            </div>

            <div className="relative">
              <div className="grid grid-cols-2 gap-6">
                <div className="space-y-6">
                  <div className="bg-gradient-to-br from-blue-500 to-cyan-500 p-8 rounded-2xl shadow-2xl text-white transform hover:scale-105 transition-transform">
                    <TrendingUp className="h-10 w-10 mb-4" />
                    <div className="text-4xl font-bold mb-2">35%</div>
                    <div className="text-blue-100">Cost Reduction</div>
                  </div>
                  <div className="bg-gradient-to-br from-purple-500 to-pink-500 p-8 rounded-2xl shadow-2xl text-white transform hover:scale-105 transition-transform">
                    <Zap className="h-10 w-10 mb-4" />
                    <div className="text-4xl font-bold mb-2">3x</div>
                    <div className="text-purple-100">Faster Processing</div>
                  </div>
                </div>
                <div className="space-y-6 mt-8">
                  <div className="bg-gradient-to-br from-orange-500 to-red-500 p-8 rounded-2xl shadow-2xl text-white transform hover:scale-105 transition-transform">
                    <Clock className="h-10 w-10 mb-4" />
                    <div className="text-4xl font-bold mb-2">15+</div>
                    <div className="text-orange-100">Hours Saved/Week</div>
                  </div>
                  <div className="bg-gradient-to-br from-green-500 to-emerald-500 p-8 rounded-2xl shadow-2xl text-white transform hover:scale-105 transition-transform">
                    <Globe className="h-10 w-10 mb-4" />
                    <div className="text-4xl font-bold mb-2">24/7</div>
                    <div className="text-green-100">Global Access</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="py-24 px-4 sm:px-6 lg:px-8 bg-gradient-to-b from-gray-50 to-white">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-20">
            <div className="inline-block mb-4">
              <span className="bg-yellow-100 text-yellow-700 text-sm font-semibold px-4 py-1.5 rounded-full">
                Testimonials
              </span>
            </div>
            <h2 className="text-5xl font-bold text-gray-900 mb-6">
              Loved by Businesses Worldwide
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              See what our customers have to say about their experience
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {testimonials.map((testimonial, index) => (
              <div
                key={index}
                className="bg-white p-8 rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 hover:-translate-y-2 border border-gray-100"
              >
                <div className="flex items-center mb-4">
                  {[...Array(testimonial.rating)].map((_, i) => (
                    <Star key={i} className="h-5 w-5 text-yellow-400 fill-current" />
                  ))}
                </div>
                <p className="text-gray-700 mb-6 leading-relaxed italic">
                  "{testimonial.content}"
                </p>
                <div className="flex items-center">
                  <div className="text-4xl mr-4">{testimonial.image}</div>
                  <div>
                    <p className="font-bold text-gray-900">{testimonial.name}</p>
                    <p className="text-sm text-gray-600">{testimonial.role}</p>
                    <p className="text-sm text-primary-600">{testimonial.company}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-24 px-4 sm:px-6 lg:px-8 bg-white">
        <div className="max-w-5xl mx-auto">
          <div className="text-center mb-16">
            <div className="inline-block mb-4">
              <span className="bg-primary-100 text-primary-700 text-sm font-semibold px-4 py-1.5 rounded-full">
                Pricing
              </span>
            </div>
            <h2 className="text-5xl font-bold text-gray-900 mb-6">
              Simple, Transparent Pricing
            </h2>
            <p className="text-xl text-gray-600">
              Start free, upgrade as you grow. No hidden fees.
            </p>
          </div>

          <div className="bg-gradient-to-br from-primary-500 via-purple-500 to-secondary-500 rounded-3xl shadow-2xl p-12 text-white relative overflow-hidden">
            <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-32 -mt-32"></div>
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-white/10 rounded-full -ml-48 -mb-48"></div>
            
            <div className="relative">
              <div className="flex items-baseline mb-8">
                <span className="text-6xl font-bold">$49</span>
                <span className="text-2xl ml-2">/month</span>
                <span className="ml-4 bg-white/20 backdrop-blur-sm px-3 py-1 rounded-full text-sm">
                  Most Popular
                </span>
              </div>
              
              <p className="text-xl mb-8 text-white/90">
                Everything you need to manage your inventory professionally
              </p>
              
              <ul className="space-y-4 mb-10">
                {pricingFeatures.map((feature, index) => (
                  <li key={index} className="flex items-center">
                    <CheckCircle className="h-6 w-6 mr-3 flex-shrink-0" />
                    <span className="text-lg">{feature}</span>
                  </li>
                ))}
              </ul>
              
              <Button
                onClick={() => navigate('/login')}
                variant="outline"
                className="w-full sm:w-auto bg-white text-primary-600 hover:bg-gray-50 border-white text-lg px-12 py-5 shadow-xl"
              >
                Start Free 14-Day Trial
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
              
              <p className="text-sm text-white/70 mt-4">
                No credit card required • Cancel anytime • 24/7 support
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="py-24 px-4 sm:px-6 lg:px-8 bg-gray-50">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-16">
            <div className="inline-block mb-4">
              <span className="bg-purple-100 text-purple-700 text-sm font-semibold px-4 py-1.5 rounded-full">
                FAQ
              </span>
            </div>
            <h2 className="text-5xl font-bold text-gray-900 mb-6">
              Frequently Asked Questions
            </h2>
            <p className="text-xl text-gray-600">
              Have questions? We have answers.
            </p>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, index) => (
              <div
                key={index}
                className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow"
              >
                <button
                  onClick={() => setOpenFaq(openFaq === index ? null : index)}
                  className="w-full px-8 py-6 text-left flex items-center justify-between hover:bg-gray-50 transition-colors"
                >
                  <span className="text-lg font-semibold text-gray-900 pr-4">
                    {faq.question}
                  </span>
                  <ChevronDown
                    className={`h-5 w-5 text-primary-600 flex-shrink-0 transition-transform duration-200 ${
                      openFaq === index ? 'transform rotate-180' : ''
                    }`}
                  />
                </button>
                {openFaq === index && (
                  <div className="px-8 pb-6">
                    <p className="text-gray-600 leading-relaxed">{faq.answer}</p>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Final CTA Section */}
      <section className="py-24 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-primary-600 via-purple-600 to-secondary-600 relative overflow-hidden">
        <div className="absolute inset-0 bg-grid-pattern opacity-10"></div>
        <div className="relative max-w-4xl mx-auto text-center">
          <h2 className="text-5xl md:text-6xl font-bold text-white mb-6">
            Ready to Get Started?
          </h2>
          <p className="text-2xl text-white/90 mb-12 leading-relaxed">
            Join 500+ businesses transforming their inventory management today.
            <span className="block mt-2">No credit card required. Free 14-day trial.</span>
          </p>
          <div className="flex flex-col sm:flex-row gap-6 justify-center">
            <Button
              onClick={() => navigate('/login')}
              variant="outline"
              className="bg-white text-primary-600 hover:bg-gray-50 border-white text-xl px-12 py-6 shadow-2xl hover:scale-105 transition-all group"
            >
              Start Free Trial
              <ArrowRight className="ml-2 h-6 w-6 group-hover:translate-x-1 transition-transform" />
            </Button>
            <Button
              onClick={() => scrollToSection('features')}
              variant="outline"
              className="text-white border-white hover:bg-white/10 text-xl px-12 py-6"
            >
              Learn More
            </Button>
          </div>
          <div className="mt-12 flex flex-wrap items-center justify-center gap-8 text-white/80">
            <div className="flex items-center">
              <CheckCircle className="h-5 w-5 mr-2" />
              <span>Free trial</span>
            </div>
            <div className="flex items-center">
              <CheckCircle className="h-5 w-5 mr-2" />
              <span>No credit card</span>
            </div>
            <div className="flex items-center">
              <CheckCircle className="h-5 w-5 mr-2" />
              <span>Cancel anytime</span>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-gray-300 py-16 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-12 mb-12">
            <div className="col-span-1 md:col-span-2">
              <div className="flex items-center space-x-3 mb-4">
                <div className="bg-gradient-to-br from-primary-500 to-secondary-500 p-2 rounded-xl">
                  <Package className="h-6 w-6 text-white" />
                </div>
                <span className="text-2xl font-bold text-white">IMS</span>
              </div>
              <p className="text-gray-400 mb-6 max-w-md">
                Modern inventory management system designed to help businesses streamline operations, reduce costs, and scale efficiently.
              </p>
              <div className="flex space-x-4">
                <a href="#" className="text-gray-400 hover:text-white transition-colors">
                  <MessageSquare className="h-5 w-5" />
                </a>
              </div>
            </div>
            
            <div>
              <h3 className="text-white font-semibold mb-4">Product</h3>
              <ul className="space-y-3">
                <li>
                  <button onClick={() => scrollToSection('features')} className="text-gray-400 hover:text-white transition-colors">
                    Features
                  </button>
                </li>
                <li>
                  <button onClick={() => scrollToSection('pricing')} className="text-gray-400 hover:text-white transition-colors">
                    Pricing
                  </button>
                </li>
                <li>
                  <button onClick={() => scrollToSection('testimonials')} className="text-gray-400 hover:text-white transition-colors">
                    Testimonials
                  </button>
                </li>
                <li>
                  <button onClick={() => scrollToSection('faq')} className="text-gray-400 hover:text-white transition-colors">
                    FAQ
                  </button>
                </li>
              </ul>
            </div>
            
            <div>
              <h3 className="text-white font-semibold mb-4">Company</h3>
              <ul className="space-y-3">
                <li>
                  <button onClick={() => navigate('/login')} className="text-gray-400 hover:text-white transition-colors">
                    Sign In
                  </button>
                </li>
                <li>
                  <button onClick={() => navigate('/login')} className="text-gray-400 hover:text-white transition-colors">
                    Get Started
                  </button>
                </li>
              </ul>
            </div>
          </div>
          
          <div className="border-t border-gray-800 pt-8">
            <div className="flex flex-col md:flex-row justify-between items-center">
              <p className="text-sm text-gray-500 mb-4 md:mb-0">
                © {new Date().getFullYear()} IMS. All rights reserved.
              </p>
              <div className="flex items-center space-x-2 text-sm text-gray-500">
                <Lock className="h-4 w-4" />
                <span>Secured by enterprise-grade encryption</span>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;
